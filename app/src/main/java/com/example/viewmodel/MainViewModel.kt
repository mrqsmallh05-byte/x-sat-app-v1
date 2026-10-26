package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.XSatApplication
import com.example.data.UserRepository
import com.example.data.model.UserEntity
import com.example.vpn.VpnStateHolder
import com.example.vpn.XSatVpnService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class AppDestination {
    SPLASH,
    LOGIN,
    REGISTER,
    MAIN_APP
}

enum class MainTab {
    HOME,
    PACKAGES,
    COUNTER,
    TERMS,
    PROFILE
}

data class SatellitePackage(
    val id: String,
    val title: String,
    val dataMb: Long,
    val pricePounds: Long,
    val durationText: String,
    val validityDays: Int,
    val category: String, // "GIFT", "MONTHLY", "FIVE_MONTHS", "YEARLY"
    val isPopular: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository = (application as XSatApplication).userRepository

    val currentUser: StateFlow<UserEntity?> = repository.currentUserFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _currentDestination = MutableStateFlow(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Auth state messages
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Satellite connection flow states
    private val _connectionDialogVisible = MutableStateFlow(false)
    val connectionDialogVisible: StateFlow<Boolean> = _connectionDialogVisible.asStateFlow()

    private val _connectionStepIndex = MutableStateFlow(0)
    val connectionStepIndex: StateFlow<Int> = _connectionStepIndex.asStateFlow()

    private val _connectionStatusMessage = MutableStateFlow("")
    val connectionStatusMessage: StateFlow<String> = _connectionStatusMessage.asStateFlow()

    // Pre-bound LPA keys list
    val preBoundLpaKeys = listOf(
        application.getString(R.string.lpa_monogoto),
        application.getString(R.string.lpa_soracom),
        application.getString(R.string.lpa_sateliot),
        application.getString(R.string.lpa_esimgo),
        application.getString(R.string.lpa_airalo),
        application.getString(R.string.lpa_globalyo),
        application.getString(R.string.lpa_keepgo)
    )

    // Pre-defined packages as requested
    val packagesList = listOf(
        // Free gift
        SatellitePackage(
            id = "pkg_free_200",
            title = "المجاني 200MB",
            dataMb = 200,
            pricePounds = 0,
            durationText = "مجاني لمدة أسبوع (7 أيام)",
            validityDays = 7,
            category = "GIFT"
        ),
        // Monthly packages
        SatellitePackage(
            id = "pkg_m_1gb",
            title = "شهر 1GB",
            dataMb = 1024,
            pricePounds = 10000,
            durationText = "صلاحية 30 يوماً",
            validityDays = 30,
            category = "MONTHLY"
        ),
        SatellitePackage(
            id = "pkg_m_15gb",
            title = "شهر 1.5GB",
            dataMb = 1536,
            pricePounds = 15000,
            durationText = "صلاحية 30 يوماً",
            validityDays = 30,
            category = "MONTHLY"
        ),
        SatellitePackage(
            id = "pkg_m_2gb",
            title = "شهر 2GB",
            dataMb = 2048,
            pricePounds = 20000,
            durationText = "صلاحية 30 يوماً",
            validityDays = 30,
            category = "MONTHLY"
        ),
        // 5 Months packages
        SatellitePackage(
            id = "pkg_5m_3gb",
            title = "5 شهور 3GB",
            dataMb = 3072,
            pricePounds = 35000,
            durationText = "صلاحية 150 يوماً",
            validityDays = 150,
            category = "FIVE_MONTHS"
        ),
        SatellitePackage(
            id = "pkg_5m_5gb",
            title = "5 شهور 5GB (عرض خاص)",
            dataMb = 5120,
            pricePounds = 35000,
            durationText = "صلاحية 150 يوماً 🔥",
            validityDays = 150,
            category = "FIVE_MONTHS",
            isPopular = true
        ),
        SatellitePackage(
            id = "pkg_5m_7gb",
            title = "5 شهور 7GB",
            dataMb = 7168,
            pricePounds = 45000,
            durationText = "صلاحية 150 يوماً",
            validityDays = 150,
            category = "FIVE_MONTHS"
        ),
        // Yearly packages
        SatellitePackage(
            id = "pkg_y_9gb",
            title = "سنة 9GB",
            dataMb = 9216,
            pricePounds = 65000,
            durationText = "صلاحية 365 يوماً 👑",
            validityDays = 365,
            category = "YEARLY"
        ),
        SatellitePackage(
            id = "pkg_y_12gb",
            title = "سنة 12GB",
            dataMb = 12288,
            pricePounds = 75000,
            durationText = "صلاحية 365 يوماً 👑",
            validityDays = 365,
            category = "YEARLY"
        ),
        SatellitePackage(
            id = "pkg_y_20gb",
            title = "سنة 20GB",
            dataMb = 20480,
            pricePounds = 100000,
            durationText = "صلاحية 365 يوماً 👑",
            validityDays = 365,
            category = "YEARLY"
        )
    )

    private val sharedPrefs = application.getSharedPreferences("xsat_alert_prefs", Context.MODE_PRIVATE)

    val isAlertEnabled: StateFlow<Boolean> = VpnStateHolder.isAlertEnabled
    val alertThresholdMb: StateFlow<Int> = VpnStateHolder.alertThresholdMb
    val isLowBalanceAlertTriggered: StateFlow<Boolean> = VpnStateHolder.isLowBalanceAlertTriggered

    val is50AlertEnabled: StateFlow<Boolean> = VpnStateHolder.is50AlertEnabled
    val is75AlertEnabled: StateFlow<Boolean> = VpnStateHolder.is75AlertEnabled
    val is90AlertEnabled: StateFlow<Boolean> = VpnStateHolder.is90AlertEnabled

    private val _currentThemeMode = MutableStateFlow(com.example.ui.theme.AppThemeMode.SPACE_DARK)
    val currentThemeMode: StateFlow<com.example.ui.theme.AppThemeMode> = _currentThemeMode.asStateFlow()

    init {
        loadAlertPreferences()
        loadThemePreferences()
        checkSessionOnStartup()
    }

    private fun loadThemePreferences() {
        val savedName = sharedPrefs.getString("key_theme_mode", com.example.ui.theme.AppThemeMode.SPACE_DARK.name)
        _currentThemeMode.value = try {
            com.example.ui.theme.AppThemeMode.valueOf(savedName ?: com.example.ui.theme.AppThemeMode.SPACE_DARK.name)
        } catch (e: Exception) {
            com.example.ui.theme.AppThemeMode.SPACE_DARK
        }
    }

    fun setThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        _currentThemeMode.value = mode
        sharedPrefs.edit().putString("key_theme_mode", mode.name).apply()
    }

    private fun loadAlertPreferences() {
        val enabled = sharedPrefs.getBoolean("key_alert_enabled", true)
        val threshold = sharedPrefs.getInt("key_alert_threshold", 20)
        VpnStateHolder.setAlertConfig(threshold, enabled)

        val e50 = sharedPrefs.getBoolean("key_alert_50_enabled", true)
        val e75 = sharedPrefs.getBoolean("key_alert_75_enabled", true)
        val e90 = sharedPrefs.getBoolean("key_alert_90_enabled", true)
        VpnStateHolder.setPercentageAlertsConfig(e50, e75, e90)
    }

    fun updateDataAlertSettings(thresholdMb: Int, enabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean("key_alert_enabled", enabled)
            .putInt("key_alert_threshold", thresholdMb)
            .apply()
        VpnStateHolder.setAlertConfig(thresholdMb, enabled)
    }

    fun updateMilestoneAlertSettings(enable50: Boolean, enable75: Boolean, enable90: Boolean) {
        sharedPrefs.edit()
            .putBoolean("key_alert_50_enabled", enable50)
            .putBoolean("key_alert_75_enabled", enable75)
            .putBoolean("key_alert_90_enabled", enable90)
            .apply()
        VpnStateHolder.setPercentageAlertsConfig(enable50, enable75, enable90)
    }

    fun triggerTestAlertNotification() {
        val app = getApplication<Application>()
        val currentBal = currentUser.value?.balanceMb ?: VpnStateHolder.remainingMb.value
        val threshold = VpnStateHolder.alertThresholdMb.value
        val sampleBal = if (currentBal <= threshold) currentBal else threshold.toLong()
        XSatVpnService.sendLowBalanceAlertNotification(app, sampleBal, threshold)
    }

    fun triggerTestMilestoneNotification(percent: Int) {
        val app = getApplication<Application>()
        val currentBal = currentUser.value?.balanceMb ?: VpnStateHolder.remainingMb.value
        val currentConsumed = currentUser.value?.consumedMb ?: VpnStateHolder.consumedMb.value
        val totalMb = (currentBal + currentConsumed.toLong()).coerceAtLeast(200L)
        val simulatedConsumed = (totalMb * (percent / 100.0))
        val simulatedRemaining = (totalMb - simulatedConsumed.toLong()).coerceAtLeast(0L)

        XSatVpnService.sendMilestoneNotification(
            context = app,
            percent = percent,
            consumedMb = simulatedConsumed,
            totalMb = totalMb,
            remainingMb = simulatedRemaining
        )
    }

    fun getDailyUsagePoints(todayLiveConsumedMb: Double): List<com.example.ui.components.DailyUsagePoint> {
        val historical = listOf(14f, 22f, 35f, 18f, 26f, 38f)
        val todayMb = (todayLiveConsumedMb.toFloat()).coerceAtLeast(8.5f)

        return listOf(
            com.example.ui.components.DailyUsagePoint("السبت", "20/10", historical[0]),
            com.example.ui.components.DailyUsagePoint("الأحد", "21/10", historical[1]),
            com.example.ui.components.DailyUsagePoint("الإثنين", "22/10", historical[2]),
            com.example.ui.components.DailyUsagePoint("الثلاثاء", "23/10", historical[3]),
            com.example.ui.components.DailyUsagePoint("الأربعاء", "24/10", historical[4]),
            com.example.ui.components.DailyUsagePoint("الخميس", "25/10", historical[5]),
            com.example.ui.components.DailyUsagePoint("اليوم", "26/10", todayMb, isToday = true)
        )
    }

    private fun checkSessionOnStartup() {
        viewModelScope.launch {
            delay(1500) // Splash duration
            val session = repository.getActiveSession()
            if (session != null) {
                val user = repository.getUserByPhone(session.userPhone)
                if (user != null && !user.isBanned) {
                    _currentDestination.value = AppDestination.MAIN_APP
                    return@launch
                }
            }
            _currentDestination.value = AppDestination.LOGIN
        }
    }

    fun navigateTo(dest: AppDestination) {
        _authError.value = null
        _currentDestination.value = dest
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun login(phone: String, pass: String) {
        val cleanPhone = phone.trim()
        if (cleanPhone.length != 10 || (!cleanPhone.startsWith("09") && !cleanPhone.startsWith("01"))) {
            _authError.value = "يرجى إدخال رقم واتساب سوداني صحيح مكون من 10 أرقام (مثال: 0912345678)"
            return
        }
        if (pass.length < 6) {
            _authError.value = "كلمة المرور يجب أن لا تقل عن 6 أحرف"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.login(cleanPhone, pass)
            _isLoading.value = false
            result.onSuccess {
                _currentDestination.value = AppDestination.MAIN_APP
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل تسجيل الدخول"
            }
        }
    }

    fun register(
        fullName: String,
        phone: String,
        region: String,
        phoneModel: String,
        pass: String,
        confirmPass: String,
        agreedToTerms: Boolean
    ) {
        val cleanName = fullName.trim()
        val cleanPhone = phone.trim()
        val cleanModel = phoneModel.trim()

        if (cleanName.isEmpty()) {
            _authError.value = "الاسم الكامل مطلوب"
            return
        }
        if (cleanPhone.length != 10 || (!cleanPhone.startsWith("09") && !cleanPhone.startsWith("01"))) {
            _authError.value = "رقم الواتساب يجب أن يكون رقماً سودانياً مكوناً من 10 أرقام (مثال: 0912345678)"
            return
        }
        if (cleanModel.isEmpty()) {
            _authError.value = "نوع التلفون مطلوب"
            return
        }
        if (pass.length < 6) {
            _authError.value = "كلمة المرور يجب أن تكون 6 أحرف على الأقل"
            return
        }
        if (pass != confirmPass) {
            _authError.value = "كلمتا المرور غير متطابقتين"
            return
        }
        if (!agreedToTerms) {
            _authError.value = "يجب الموافقة على سياسة الخصوصية وشروط الاستخدام أولاً"
            return
        }

        val deviceId = "${Build.MANUFACTURER}_${Build.MODEL}"

        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null
            val result = repository.registerUser(
                fullName = cleanName,
                phone = cleanPhone,
                region = region,
                phoneModel = cleanModel,
                plainPassword = pass,
                deviceId = deviceId
            )
            _isLoading.value = false
            result.onSuccess {
                _currentDestination.value = AppDestination.MAIN_APP
            }.onFailure { err ->
                _authError.value = err.message ?: "فشل إنشاء الحساب"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            stopVpn()
            _currentDestination.value = AppDestination.LOGIN
            _currentTab.value = MainTab.HOME
        }
    }

    fun startSatelliteConnectionProcess(context: Context, onVpnPermissionRequired: () -> Unit) {
        val user = currentUser.value ?: return

        if (user.balanceMb <= 0) {
            _connectionStatusMessage.value = "انتهت الباقة! يرجى تجديد الاشتراك من صفحة الباقات"
            selectTab(MainTab.PACKAGES)
            return
        }

        if (System.currentTimeMillis() > user.expiryTimestamp) {
            _connectionStatusMessage.value = "انتهت صلاحية الباقة! يرجى تجديد الاشتراك"
            selectTab(MainTab.PACKAGES)
            return
        }

        viewModelScope.launch {
            _connectionDialogVisible.value = true
            _connectionStepIndex.value = 1
            _connectionStatusMessage.value = "جاري فحص وتحديد مدار القمر الصناعي..."
            delay(1000)

            _connectionStepIndex.value = 2
            _connectionStatusMessage.value = "تحديد زاوية الاستقبال في منطقة ${user.region}..."
            delay(1000)

            _connectionStepIndex.value = 3
            _connectionStatusMessage.value = "انت رقم 3 في طابور الاتصال بالقمر - الانتظار قليل..."
            VpnStateHolder.setConnecting(true, "انت في الطابور", 3)
            delay(1200)

            _connectionStepIndex.value = 4
            _connectionStatusMessage.value = "ربط حزمة L-Band المشفرة عبر Monogoto Sateliot..."
            delay(1000)

            _connectionStepIndex.value = 5
            _connectionStatusMessage.value = "تم الاتصال بنجاح! تم تفعيل نت القمر"
            delay(800)

            _connectionDialogVisible.value = false
            VpnStateHolder.setConnecting(false)

            // Start VpnService
            try {
                XSatVpnService.start(
                    context = context,
                    userPhone = user.phone,
                    balanceMb = user.balanceMb,
                    consumedMb = user.consumedMb
                )
                // Switch to screen 2 or 3 as per concept
                selectTab(MainTab.COUNTER)
            } catch (e: Exception) {
                _connectionStatusMessage.value = "القمر غير متاح حالياً - حاول في مكان مفتوح"
            }
        }
    }

    fun stopVpn() {
        val context = getApplication<Application>()
        XSatVpnService.stop(context)
    }

    fun activatePaidPackage(
        selectedPackage: SatellitePackage,
        receiptUri: String?,
        onSuccess: () -> Unit
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            delay(5000) // 5 seconds simulation as requested: "After 5 sec simulation: تم التأكيد - تم تفعيل الباقة"
            val result = repository.activatePaidPackage(
                phone = user.phone,
                packageName = selectedPackage.title,
                addedMb = selectedPackage.dataMb,
                validityDays = selectedPackage.validityDays,
                amountPounds = selectedPackage.pricePounds,
                receiptUri = receiptUri
            )
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }
        }
    }

    fun formatRemainingTime(expiryTimestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = expiryTimestamp - now
        if (diff <= 0) return "انتهت الصلاحية"

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

        return if (days > 0) {
            "متبقي $days يوم و $hours ساعة"
        } else if (hours > 0) {
            "متبقي $hours ساعة و $minutes دقيقة"
        } else {
            "متبقي $minutes دقيقة"
        }
    }
}
