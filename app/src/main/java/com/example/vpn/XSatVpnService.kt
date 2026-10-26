package com.example.vpn

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.XSatApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

class XSatVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private var userPhone: String = ""
    private var currentBalanceMb: Long = 200
    private var currentConsumedMb: Double = 0.0
    private var hasTriggeredLowBalanceAlert: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START
        when (action) {
            ACTION_START -> {
                userPhone = intent?.getStringExtra(EXTRA_USER_PHONE) ?: ""
                currentBalanceMb = intent?.getLongExtra(EXTRA_BALANCE_MB, 200L) ?: 200L
                currentConsumedMb = intent?.getDoubleExtra(EXTRA_CONSUMED_MB, 0.0) ?: 0.0
                hasTriggeredLowBalanceAlert = false
                VpnStateHolder.resetMilestones()
                startVpn()
            }
            ACTION_STOP -> {
                stopVpn()
            }
            ACTION_UPDATE_BALANCE -> {
                val newBal = intent?.getLongExtra(EXTRA_BALANCE_MB, currentBalanceMb) ?: currentBalanceMb
                currentBalanceMb = newBal
                if (currentBalanceMb > VpnStateHolder.alertThresholdMb.value) {
                    hasTriggeredLowBalanceAlert = false
                }
                updateNotification()
            }
        }
        return START_NOT_STICKY
    }

    private fun startVpn() {
        if (vpnInterface != null) return

        try {
            val builder = Builder()
                .setSession("X-SAT Satellite L-Band")
                .addAddress("10.8.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .addDnsServer("1.1.1.1")
                .setMtu(1500)

            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                startForeground(NOTIFICATION_ID, buildNotification())
                VpnStateHolder.setConnected(true)
                startTrafficWorker()
                Log.d(TAG, "X-SAT Satellite VPN established successfully.")
            } else {
                Log.e(TAG, "Failed to establish VPN interface (null returned).")
                stopSelf()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while starting X-SAT VPN: ${e.message}", e)
            stopSelf()
        }
    }

    private fun startTrafficWorker() {
        job?.cancel()
        job = serviceScope.launch {
            var totalBytesRx = 0L
            var totalBytesTx = 0L

            var lastSyncTime = System.currentTimeMillis()
            var sessionConsumedDelta = 0.0

            while (isActive && vpnInterface != null) {
                try {
                    // Check balance cutoff
                    if (currentBalanceMb <= 0) {
                        Log.w(TAG, "Balance reached 0MB. Halting VPN.")
                        stopVpn()
                        break
                    }

                    // Dynamic satellite speed emulation (between 1.8 Mbps and 3.4 Mbps)
                    val jitter = Random.nextDouble(1.8, 3.4)
                    val packetBatchBytes = (jitter * 125_000 * 0.5).toLong() // Bytes in half second

                    totalBytesRx += (packetBatchBytes * 0.75).toLong()
                    totalBytesTx += (packetBatchBytes * 0.25).toLong()

                    val deltaMb = packetBatchBytes / (1024.0 * 1024.0)
                    sessionConsumedDelta += deltaMb

                    currentConsumedMb += deltaMb
                    if (currentBalanceMb > 0) {
                        currentBalanceMb = (currentBalanceMb - deltaMb.toLong()).coerceAtLeast(0L)
                    }

                    // 1. Check fixed low balance alert (e.g. <= 20MB)
                    val alertThreshold = VpnStateHolder.alertThresholdMb.value
                    val alertEnabled = VpnStateHolder.isAlertEnabled.value
                    if (alertEnabled && currentBalanceMb <= alertThreshold && !hasTriggeredLowBalanceAlert && currentBalanceMb > 0) {
                        hasTriggeredLowBalanceAlert = true
                        VpnStateHolder.setLowBalanceAlertTriggered(true)
                        sendLowBalanceAlertNotification(this@XSatVpnService, currentBalanceMb, alertThreshold)
                    }

                    // 2. Check milestone percentage alerts (50%, 75%, 90%)
                    val totalPackageMb = (currentBalanceMb + currentConsumedMb.toLong()).coerceAtLeast(1L)
                    val consumedPercent = ((currentConsumedMb / totalPackageMb.toDouble()) * 100).toInt()

                    if (VpnStateHolder.is50AlertEnabled.value && consumedPercent >= 50 && 50 !in VpnStateHolder.triggeredMilestones.value) {
                        VpnStateHolder.markMilestoneTriggered(50)
                        sendMilestoneNotification(this@XSatVpnService, 50, currentConsumedMb, totalPackageMb, currentBalanceMb)
                    }

                    if (VpnStateHolder.is75AlertEnabled.value && consumedPercent >= 75 && 75 !in VpnStateHolder.triggeredMilestones.value) {
                        VpnStateHolder.markMilestoneTriggered(75)
                        sendMilestoneNotification(this@XSatVpnService, 75, currentConsumedMb, totalPackageMb, currentBalanceMb)
                    }

                    if (VpnStateHolder.is90AlertEnabled.value && consumedPercent >= 90 && 90 !in VpnStateHolder.triggeredMilestones.value) {
                        VpnStateHolder.markMilestoneTriggered(90)
                        sendMilestoneNotification(this@XSatVpnService, 90, currentConsumedMb, totalPackageMb, currentBalanceMb)
                    }

                    VpnStateHolder.updateMetrics(
                        rx = totalBytesRx,
                        tx = totalBytesTx,
                        speed = String.format("%.1f", jitter).toDoubleOrNull() ?: 2.4,
                        remaining = currentBalanceMb,
                        consumed = currentConsumedMb
                    )

                    // Periodically persist to Room DB and update Notification every 3 seconds
                    val now = System.currentTimeMillis()
                    if (now - lastSyncTime >= 3000) {
                        lastSyncTime = now
                        updateNotification()

                        if (userPhone.isNotEmpty()) {
                            try {
                                val repo = XSatApplication.instance.userRepository
                                repo.updateInternetConsumption(userPhone, sessionConsumedDelta)
                                sessionConsumedDelta = 0.0
                            } catch (e: Exception) {
                                Log.e(TAG, "Error updating usage in Room: ${e.message}")
                            }
                        }
                    }

                    delay(500)
                } catch (e: Exception) {
                    Log.e(TAG, "Worker loop exception: ${e.message}")
                    delay(1000)
                }
            }
        }
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, flags)

        val balanceDisplay = if (currentBalanceMb >= 1024) {
            "${String.format("%.1f", currentBalanceMb / 1024.0)} GB"
        } else {
            "$currentBalanceMb MB"
        }

        return NotificationCompat.Builder(this, XSatApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔑 X-SAT متصل - باقي $balanceDisplay")
            .setContentText("انترنت القمر الفضائي نشط عبر نطاق L-Band المباشر")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification() {
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.notify(NOTIFICATION_ID, buildNotification())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update notification: ${e.message}")
        }
    }

    private fun stopVpn() {
        job?.cancel()
        job = null
        try {
            vpnInterface?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing VPN interface: ${e.message}")
        }
        vpnInterface = null
        VpnStateHolder.setConnected(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    companion object {
        const val TAG = "XSatVpnService"
        const val NOTIFICATION_ID = 1001
        const val ALERT_NOTIFICATION_ID = 2002
        const val MILESTONE_ALERT_BASE_ID = 3000

        const val ACTION_START = "com.example.vpn.ACTION_START"
        const val ACTION_STOP = "com.example.vpn.ACTION_STOP"
        const val ACTION_UPDATE_BALANCE = "com.example.vpn.ACTION_UPDATE_BALANCE"

        const val EXTRA_USER_PHONE = "extra_user_phone"
        const val EXTRA_BALANCE_MB = "extra_balance_mb"
        const val EXTRA_CONSUMED_MB = "extra_consumed_mb"

        fun start(context: Context, userPhone: String, balanceMb: Long, consumedMb: Double) {
            val intent = Intent(context, XSatVpnService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_USER_PHONE, userPhone)
                putExtra(EXTRA_BALANCE_MB, balanceMb)
                putExtra(EXTRA_CONSUMED_MB, consumedMb)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, XSatVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun sendLowBalanceAlertNotification(context: Context, remainingMb: Long, thresholdMb: Int) {
            try {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("NAVIGATE_TAB", "PACKAGES")
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getActivity(context, 102, intent, flags)

                val title = context.getString(R.string.data_alert_notification_title)
                val content = context.getString(R.string.data_alert_notification_content, remainingMb, thresholdMb)

                val notification = NotificationCompat.Builder(context, XSatApplication.CHANNEL_ALERT_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                manager.notify(ALERT_NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send data alert notification: ${e.message}")
            }
        }

        fun sendMilestoneNotification(
            context: Context,
            percent: Int,
            consumedMb: Double,
            totalMb: Long,
            remainingMb: Long
        ) {
            try {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("NAVIGATE_TAB", "PACKAGES")
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getActivity(context, 200 + percent, intent, flags)

                val (title, content) = when (percent) {
                    50 -> Pair(
                        context.getString(R.string.data_alert_50_title),
                        context.getString(R.string.data_alert_50_desc, consumedMb, totalMb, remainingMb)
                    )
                    75 -> Pair(
                        context.getString(R.string.data_alert_75_title),
                        context.getString(R.string.data_alert_75_desc, remainingMb)
                    )
                    else -> Pair(
                        context.getString(R.string.data_alert_90_title),
                        context.getString(R.string.data_alert_90_desc, remainingMb)
                    )
                }

                val notification = NotificationCompat.Builder(context, XSatApplication.CHANNEL_ALERT_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build()

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                manager.notify(MILESTONE_ALERT_BASE_ID + percent, notification)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send milestone notification: ${e.message}")
            }
        }
    }
}
