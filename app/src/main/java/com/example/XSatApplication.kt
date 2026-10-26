package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.AppDatabase
import com.example.data.UserRepository

class XSatApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        userRepository = UserRepository(
            userDao = database.userDao(),
            sessionDao = database.sessionDao(),
            transactionDao = database.transactionDao()
        )

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Low importance for persistent VPN ongoing foreground notification
            val vpnChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.vpn_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.vpn_notification_desc)
                setShowBadge(false)
            }
            manager.createNotificationChannel(vpnChannel)

            // High importance for low balance data alert notification
            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                getString(R.string.data_alert_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.data_alert_channel_desc)
                enableVibration(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "xsat_vpn_status_channel"
        const val CHANNEL_ALERT_ID = "xsat_data_alert_channel"
        lateinit var instance: XSatApplication
            private set
    }
}
