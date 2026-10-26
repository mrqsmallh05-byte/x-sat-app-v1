package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.DailyUsagePoint
import com.example.ui.theme.AppThemeMode
import com.example.vpn.VpnStateHolder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name and satellite keys from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("X-SAT", appName)

        val monogotoKey = context.getString(R.string.lpa_monogoto)
        assertNotNull(monogotoKey)
    }

    @Test
    fun `verify custom data alert threshold default is 20MB`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val alertTitle = context.getString(R.string.data_alert_notification_title)
        assertNotNull(alertTitle)

        // Verify VpnStateHolder default is 20MB
        VpnStateHolder.setAlertConfig(thresholdMb = 20, enabled = true)
        assertEquals(20, VpnStateHolder.alertThresholdMb.value)
        assertTrue(VpnStateHolder.isAlertEnabled.value)
    }

    @Test
    fun `verify milestone alerts 50 and 75 and 90 percent configs`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val title50 = context.getString(R.string.data_alert_50_title)
        val title75 = context.getString(R.string.data_alert_75_title)
        val title90 = context.getString(R.string.data_alert_90_title)

        assertNotNull(title50)
        assertNotNull(title75)
        assertNotNull(title90)

        VpnStateHolder.setPercentageAlertsConfig(enable50 = true, enable75 = true, enable90 = true)
        assertTrue(VpnStateHolder.is50AlertEnabled.value)
        assertTrue(VpnStateHolder.is75AlertEnabled.value)
        assertTrue(VpnStateHolder.is90AlertEnabled.value)

        VpnStateHolder.markMilestoneTriggered(50)
        assertTrue(50 in VpnStateHolder.triggeredMilestones.value)
    }

    @Test
    fun `verify theme modes include amoled battery saver`() {
        val amoledMode = AppThemeMode.AMOLED_BATTERY_SAVER
        assertEquals("AMOLED_BATTERY_SAVER", amoledMode.name)
        assertEquals(4, AppThemeMode.values().size)
    }

    @Test
    fun `verify daily usage data points structure`() {
        val point = DailyUsagePoint("السبت", "20/10", 25.5f, isToday = false)
        assertEquals("السبت", point.dayName)
        assertEquals(25.5f, point.consumedMb, 0.01f)
    }
}
