package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.SatelliteVisual
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
fun HomeScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    onNavigateToPackages: () -> Unit,
    onRequestSatelliteNet: () -> Unit
) {
    val isConnected by VpnStateHolder.isConnected.collectAsState()
    val isConnecting by VpnStateHolder.isConnecting.collectAsState()
    val remainingMb by VpnStateHolder.remainingMb.collectAsState()
    val currentSpeed by VpnStateHolder.currentSpeedMbps.collectAsState()

    val connectionDialogVisible by viewModel.connectionDialogVisible.collectAsState()
    val connectionStepIndex by viewModel.connectionStepIndex.collectAsState()
    val connectionStatusMessage by viewModel.connectionStatusMessage.collectAsState()

    val effectiveBalance = currentUser?.balanceMb ?: remainingMb

    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val buttonGlowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonGlow"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مرحباً، ${currentUser?.fullName ?: "مستخدم X-SAT"}",
                    style = MaterialTheme.typography.titleLarge,
                    color = SatWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "المنطقة: ${currentUser?.region ?: "ابوحمد"} • ${currentUser?.phoneModel ?: "Huawei"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SatTextMuted
                )
            }

            // Connection Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isConnected) SatPrimary.copy(alpha = 0.2f) else SatRed.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isConnected) SatPrimary else SatRed
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) SatPrimary else SatRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isConnected) "🟢 متصل بالقمر - باقي ${effectiveBalance}MB" else "🔴 غير متصل",
                        color = if (isConnected) SatPrimary else SatRed,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Rotating Satellite Dish
        SatelliteVisual(
            size = 210.dp,
            isActive = isConnected || isConnecting,
            speedMultiplier = if (isConnected) 2f else 1f
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Center Big Glowing Button: [📡 اطلب نت من القمر]
        Box(
            modifier = Modifier
                .scale(if (!isConnected) buttonGlowScale else 1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (isConnected) {
                        viewModel.stopVpn()
                    } else {
                        onRequestSatelliteNet()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) SatRed else SatPrimary,
                    contentColor = if (isConnected) SatWhite else SatBackground
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .shadow(
                        elevation = if (isConnected) 8.dp else 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = if (isConnected) SatRed else SatPrimary
                    )
                    .testTag("request_satellite_net_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.WifiOff else Icons.Default.SatelliteAlt,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isConnected) "⏸️ فصل نت القمر" else "📡 اطلب نت من القمر",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Text below button: "الهدية مجانية لمدة اسبوع فقط"
        Text(
            text = "الهدية مجانية لمدة اسبوع فقط (200MB فورية بدون أي إعدادات)",
            style = MaterialTheme.typography.bodyMedium,
            color = SatGold,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Gift & Status Glass Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToPackages() },
            isGlowing = true,
            glowColor = SatPrimary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SatPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "الهدية",
                            tint = SatPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "هدية الاستقبال المجانية",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "رصيدك: ${effectiveBalance}MB • ${viewModel.formatRemainingTime(currentUser?.expiryTimestamp ?: 0)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SatPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SatPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = "عرض الباقات",
                        color = SatBackground,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Satellite Orbit Telemetry Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "بيانات المدار الفضائي",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monogoto Sateliot L-Band",
                        style = MaterialTheme.typography.labelMedium,
                        color = SatGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("التردد الحامل", color = SatTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("1.62 GHz (L-Band)", color = SatWhite, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("السرعة المقدرة", color = SatTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text(if (isConnected) "$currentSpeed Mbps" else "2.4 Mbps", color = SatPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("تشفير LPA", color = SatTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("مربوط داخلياً (مؤمن)", color = SatWhite, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Interactive Connection Progress Modal
    if (connectionDialogVisible) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SatCard,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SatPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SatelliteVisual(size = 120.dp, isActive = true, speedMultiplier = 2.5f)

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "جاري الاتصال بالقمر...",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = connectionStatusMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = SatPrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LinearProgressStepIndicator(currentStep = connectionStepIndex, totalSteps = 5)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "انت رقم 3 في الطابور - الانتظار قليل جداً...",
                        style = MaterialTheme.typography.labelMedium,
                        color = SatGold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun LinearProgressStepIndicator(currentStep: Int, totalSteps: Int) {
    val progress = (currentStep.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SatCardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(SatPrimaryVariant, SatPrimary)
                    )
                )
        )
    }
}
