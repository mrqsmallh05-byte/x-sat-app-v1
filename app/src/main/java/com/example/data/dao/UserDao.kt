package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    fun getUserByPhoneFlow(phone: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    fun getLatestUserFlow(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balanceMb = :balanceMb, consumedMb = :consumedMb WHERE phone = :phone")
    suspend fun updateUsage(phone: String, balanceMb: Long, consumedMb: Double)

    @Query("UPDATE users SET balanceMb = balanceMb + :addedMb, expiryTimestamp = :newExpiry, activePackageName = :packageName WHERE phone = :phone")
    suspend fun addPackage(phone: String, packageName: String, addedMb: Long, newExpiry: Long)

    @Query("UPDATE users SET isBanned = :banned WHERE phone = :phone")
    suspend fun setBanned(phone: String, banned: Boolean)

    @Query("DELETE FROM users WHERE phone = :phone")
    suspend fun deleteUser(phone: String)
}
