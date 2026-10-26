package com.example.data

import com.example.data.dao.SessionDao
import com.example.data.dao.TransactionDao
import com.example.data.dao.UserDao
import com.example.data.model.PackageTransactionEntity
import com.example.data.model.SessionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao,
    private val sessionDao: SessionDao,
    private val transactionDao: TransactionDao
) {

    val activeSessionFlow: Flow<SessionEntity?> = sessionDao.getActiveSessionFlow()

    val currentUserFlow: Flow<UserEntity?> = activeSessionFlow.flatMapLatest { session ->
        if (session != null) {
            userDao.getUserByPhoneFlow(session.userPhone)
        } else {
            flowOf(null)
        }
    }

    suspend fun getUserByPhone(phone: String): UserEntity? = userDao.getUserByPhone(phone)

    suspend fun getActiveSession(): SessionEntity? = sessionDao.getActiveSession()

    suspend fun registerUser(
        fullName: String,
        phone: String,
        region: String,
        phoneModel: String,
        plainPassword: String,
        deviceId: String
    ): Result<UserEntity> {
        val existing = userDao.getUserByPhone(phone)
        if (existing != null) {
            return Result.failure(Exception("هذا الرقم مسجل مسبقاً - يرجى تسجيل الدخول"))
        }

        val hashedPassword = hashPassword(plainPassword)
        // Free gift valid for 7 days (7 * 24 * 60 * 60 * 1000)
        val sevenDaysMs = 7L * 24 * 60 * 60 * 1000
        val expiryTime = System.currentTimeMillis() + sevenDaysMs

        val newUser = UserEntity(
            fullName = fullName.trim(),
            phone = phone.trim(),
            region = region.trim(),
            phoneModel = phoneModel.trim(),
            password = hashedPassword,
            balanceMb = 200,
            consumedMb = 0.0,
            expiryTimestamp = expiryTime,
            registeredAt = System.currentTimeMillis(),
            isBanned = false,
            deviceId = deviceId,
            activePackageName = "المجاني 200MB",
            hasClaimedFreeGift = true
        )

        val id = userDao.insertUser(newUser)
        sessionDao.setSession(SessionEntity(userPhone = newUser.phone))
        return Result.success(newUser.copy(id = id))
    }

    suspend fun login(phone: String, plainPassword: String): Result<UserEntity> {
        val user = userDao.getUserByPhone(phone.trim())
            ?: return Result.failure(Exception("رقم الواتساب غير مسجل، يرجى إنشاء حساب جديد"))

        if (user.isBanned) {
            return Result.failure(Exception("تم حظر هذا الحساب لمخالفة شروط الاستخدام"))
        }

        val hashed = hashPassword(plainPassword)
        if (user.password != hashed) {
            return Result.failure(Exception("كلمة المرور غير صحيحة"))
        }

        sessionDao.setSession(SessionEntity(userPhone = user.phone))
        return Result.success(user)
    }

    suspend fun logout() {
        sessionDao.clearSession()
    }

    suspend fun updateInternetConsumption(phone: String, consumedDeltaMb: Double) {
        val user = userDao.getUserByPhone(phone) ?: return
        val newConsumed = user.consumedMb + consumedDeltaMb
        val newBalance = (user.balanceMb - consumedDeltaMb.toLong()).coerceAtLeast(0L)
        userDao.updateUsage(phone, newBalance, newConsumed)
    }

    suspend fun activatePaidPackage(
        phone: String,
        packageName: String,
        addedMb: Long,
        validityDays: Int,
        amountPounds: Long,
        receiptUri: String? = null
    ): Result<Unit> {
        val user = userDao.getUserByPhone(phone)
            ?: return Result.failure(Exception("المستخدم غير موجود"))

        val currentExpiry = if (user.expiryTimestamp > System.currentTimeMillis()) {
            user.expiryTimestamp
        } else {
            System.currentTimeMillis()
        }
        val addedTimeMs = validityDays.toLong() * 24 * 60 * 60 * 1000
        val newExpiry = currentExpiry + addedTimeMs

        userDao.addPackage(
            phone = phone,
            packageName = packageName,
            addedMb = addedMb,
            newExpiry = newExpiry
        )

        transactionDao.insertTransaction(
            PackageTransactionEntity(
                userPhone = phone,
                packageName = packageName,
                amountSudanPounds = amountPounds,
                dataMb = addedMb,
                validityDays = validityDays,
                receiptImageUri = receiptUri,
                status = "CONFIRMED"
            )
        )

        return Result.success(Unit)
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
