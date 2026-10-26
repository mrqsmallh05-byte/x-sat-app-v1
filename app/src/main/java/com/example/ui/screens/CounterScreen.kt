package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatPrimaryVariant
import com.example.ui.theme.SatRed
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite
import com.example.viewmodel.MainViewModel
import com.example.vpn.VpnStateHolder

@Composable
fun CounterScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    onNavigateToPackages: () -> Unit,
    onStartVpn: () -> Unit
) {
    val isConnected by VpnStateHolder.isConnected.collectAsState()
    val liveSpeed by VpnStateHolder.currentSpeedMbps.collectAsState()
    val bytesReceived by VpnStateHolder.bytesReceived.collectAsState()
    val bytesSent by VpnStateHolder.bytesSent.collectAsState()
    val activeCarrier by VpnStateHolder.activeCarrier.collectAsState()

    val isAlertEnabled by viewModel.isAlertEnabled.collectAsState()
    val alertThresholdMb by viewModel.alertThresholdMb.collectAsState()
    val isAlertTriggered by viewModel.isLowBalanceAlertTriggered.collectAsState()

    val is50AlertEnabled by viewModel.is50AlertEnabled.collectAsState()
    val is75AlertEnabled by viewModel.is75AlertEnabled.collectAsState()
    val is90AlertEnabled by viewModel.is90AlertEnabled.collectAsState()

    val balanceMb = currentUser?.balanceMb ?: 0L
    val consumedMb = currentUser?.consumedMb ?: 0.0
    val totalMb = (balanceMb + consumedMb.toLong()).coerceAtLeast(1L)
    val remainingPercentage = ((balanceMb.toFloat() / totalMb.toFloat()) * 100).coerceIn(0f, 100f)

    val isPackageEnded = balanceMb <= 0 || (currentUser?.expiryTimestamp ?: 0) < System.currentTimeMillis()
    val isBelowAlertThreshold = isAlertEnabled && balanceMb <= alertThresholdMb && balanceMb > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "انترنتي والعداد المباشر",
            style = MaterialTheme.typography.displayMedium,
            color = SatWhite,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "مراقبة استهلاك باقة القمر الصناعي في الوقت الفعلي",
            style = MaterialTheme.typography.bodyMedium,
            color = SatTextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Big Circular Neon Green Progress
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 16.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                // Background Track
                drawArc(
                    color = SatCardBorder,
                    startAngle = 140f,
                    sweepAngle = 260f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Progress Arc
                val sweep = (remainingPercentage / 100f) * 260f
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(SatPrimaryVariant, SatPrimary, SatGold)
                    ),
                    startAngle = 140f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${remainingPercentage.toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = if (isPackageEnded) SatRed else if (isBelowAlertThreshold) SatGold else SatPrimary,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = if (isConnected) "🟢 نشط الآن" else "⏸️ متوقف",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isConnected) SatPrimary else SatTextMuted,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "السرعة: ${if (isConnected) liveSpeed else 0.0} Mbps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SatGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active Low Balance Alert Warning Banner
        if (isBelowAlertThreshold) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isGlowing = true,
                glowColor = SatGold,
                borderWidth = 1.5.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = SatGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚠️ انتبه: الرصيد أقل من ${alertThresholdMb}MB!",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "المتبقي ${balanceMb}MB فقط. تم إطلاق تنبيه محلي لتفادي انقطاع نت القمر.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SatWhite
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // When package ends banner
        if (isPackageEnded) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isGlowing = true,
                glowColor = SatRed,
                borderWidth = 1.5.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = SatRed,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "انتهت الباقة - جدد الآن",
                        style = MaterialTheme.typography.titleLarge,
                        color = SatRed,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "لقد استنفدت كامل رصيدك أو انتهت فترة الصلاحية. اضغط بالأسفل لاختيار باقة جديدة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SatWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToPackages,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SatPrimary,
                            contentColor = SatBackground
                        )
                    ) {
                        Text("الانتقال لصفحة الباقات", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Real Telemetry Breakdown Glass Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TelemetryRow(
                    label = "الباقة الحالية:",
                    value = currentUser?.activePackageName ?: "المجاني 200MB",
                    valueColor = SatWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow(
                    label = "استهلكت:",
                    value = "${String.format("%.1f", consumedMb)}MB / ${totalMb}MB",
                    valueColor = SatGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow(
                    label = "متبقي:",
                    value = "${balanceMb}MB",
                    valueColor = if (isBelowAlertThreshold) SatGold else SatPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow(
                    label = "الصلاحية:",
                    value = viewModel.formatRemainingTime(currentUser?.expiryTimestamp ?: 0),
                    valueColor = SatWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow(
                    label = "السرعة:",
                    value = "${if (isConnected) liveSpeed else 0.0} Mbps",
                    valueColor = SatPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                TelemetryRow(
                    label = "حزم البيانات:",
                    value = "استقبال: ${bytesReceived / 1024} KB • إرسال: ${bytesSent / 1024} KB",
                    valueColor = SatTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Daily Data Usage Chart (معدل استهلاك البيانات اليومي)
        val dailyUsagePoints = viewModel.getDailyUsagePoints(consumedMb)
        com.example.ui.components.DailyUsageChart(
            modifier = Modifier.fillMaxWidth(),
            dailyPoints = dailyUsagePoints
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Custom Data Usage Alert Settings Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isGlowing = isAlertEnabled,
            glowColor = SatPrimary
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SatPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنبيه استهلاك الرصيد المخصص",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Switch(
                        checked = isAlertEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.updateDataAlertSettings(alertThresholdMb, enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SatBackground,
                            checkedTrackColor = SatPrimary,
                            uncheckedThumbColor = SatTextMuted,
                            uncheckedTrackColor = SatCardBorder
                        ),
                        modifier = Modifier.testTag("toggle_data_alert")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "يطلق إشعاراً محلياً منبثقاً عند انخفاض الرصيد المجاني المتبقي إلى ${alertThresholdMb}MB لتنبيهك قبل انقطاع النت.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SatTextMuted
                )

                if (isAlertEnabled) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "حد التنبيه المطلوب:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SatWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SatPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SatPrimary)
                        ) {
                            Text(
                                text = "${alertThresholdMb} MB",
                                style = MaterialTheme.typography.titleMedium,
                                color = SatPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset threshold chips
                    val presetThresholds = listOf(10, 20, 30, 50)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetThresholds.forEach { preset ->
                            val isSelected = alertThresholdMb == preset
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateDataAlertSettings(preset, true)
                                },
                                label = {
                                    Text(
                                        text = if (preset == 20) "$preset MB (الافتراضي)" else "$preset MB",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SatPrimary,
                                    selectedLabelColor = SatBackground,
                                    containerColor = SatCardBorder.copy(alpha = 0.5f),
                                    labelColor = SatWhite
                                ),
                                modifier = Modifier.testTag("chip_alert_threshold_$preset")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider for fine tuning threshold
                    Slider(
                        value = alertThresholdMb.toFloat(),
                        onValueChange = { newValue ->
                            viewModel.updateDataAlertSettings(newValue.toInt(), true)
                        },
                        valueRange = 5f..100f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = SatPrimary,
                            activeTrackColor = SatPrimary,
                            inactiveTrackColor = SatCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_alert_threshold")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.triggerTestAlertNotification() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SatPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_alert_notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SatPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تجربة إشعار التنبيه المخصص (${alertThresholdMb}MB)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Percentage Milestone Alerts (تنبيهات استهلاك 50% و 75% و 90%)
                    Text(
                        text = "تنبيهات مراحل الاستهلاك (50% • 75% • 90%):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MilestoneToggleRow(
                        title = "تنبيه استهلاك 50% من الباقة",
                        desc = "إشعار محلي عند استهلاك نصف سعة الباقة",
                        checked = is50AlertEnabled,
                        onCheckedChange = { viewModel.updateMilestoneAlertSettings(it, is75AlertEnabled, is90AlertEnabled) },
                        onTestClick = { viewModel.triggerTestMilestoneNotification(50) },
                        testTag = "toggle_milestone_50"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MilestoneToggleRow(
                        title = "تنبيه استهلاك 75% من الباقة",
                        desc = "إشعار تنبيهي عند استهلاك 75% واقتراب النفاد",
                        checked = is75AlertEnabled,
                        onCheckedChange = { viewModel.updateMilestoneAlertSettings(is50AlertEnabled, it, is90AlertEnabled) },
                        onTestClick = { viewModel.triggerTestMilestoneNotification(75) },
                        testTag = "toggle_milestone_75"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MilestoneToggleRow(
                        title = "تحذير عاجل عند استهلاك 90% من الباقة",
                        desc = "إشعار حاسم عند بقاء 10% لتجديد الاشتراك فوراً",
                        checked = is90AlertEnabled,
                        onCheckedChange = { viewModel.updateMilestoneAlertSettings(is50AlertEnabled, is75AlertEnabled, it) },
                        onTestClick = { viewModel.triggerTestMilestoneNotification(90) },
                        testTag = "toggle_milestone_90"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: [⏸️ ايقاف النت] [▶️ تشغيل النت]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = { viewModel.stopVpn() },
                enabled = isConnected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SatRed,
                    contentColor = SatWhite,
                    disabledContainerColor = SatCardBorder,
                    disabledContentColor = SatTextMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("stop_internet_button")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("⏸️ ايقاف النت", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onStartVpn,
                enabled = !isConnected && !isPackageEnded,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SatPrimary,
                    contentColor = SatBackground,
                    disabledContainerColor = SatCardBorder,
                    disabledContentColor = SatTextMuted
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("start_internet_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("▶️ تشغيل النت", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun TelemetryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SatTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MilestoneToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTestClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SatBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, SatCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.labelSmall,
                        color = SatTextMuted
                    )
                }

                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SatBackground,
                        checkedTrackColor = SatPrimary,
                        uncheckedThumbColor = SatTextMuted,
                        uncheckedTrackColor = SatCardBorder
                    ),
                    modifier = Modifier.testTag(testTag)
                )
            }

            if (checked) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "تجربة الإشعار 🔔",
                        style = MaterialTheme.typography.labelSmall,
                        color = SatPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onTestClick() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

