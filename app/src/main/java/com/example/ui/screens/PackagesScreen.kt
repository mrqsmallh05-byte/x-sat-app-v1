package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.PaymentDialog
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatRed
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SatellitePackage
import com.example.vpn.VpnStateHolder

@Composable
fun PackagesScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    onRequestActivateFree: () -> Unit
) {
    val isConnected by VpnStateHolder.isConnected.collectAsState()
    var selectedPackageForPayment by remember { mutableStateOf<SatellitePackage?>(null) }
    var showActivationSuccessAlert by remember { mutableStateOf(false) }

    val freePackage = viewModel.packagesList.first { it.category == "GIFT" }
    val monthlyPackages = viewModel.packagesList.filter { it.category == "MONTHLY" }
    val fiveMonthsPackages = viewModel.packagesList.filter { it.category == "FIVE_MONTHS" }
    val yearlyPackages = viewModel.packagesList.filter { it.category == "YEARLY" }

    val userBalance = currentUser?.balanceMb ?: 0L
    val isGiftExpired = (currentUser?.expiryTimestamp ?: 0) < System.currentTimeMillis() || userBalance <= 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "باقات X-SAT",
                style = MaterialTheme.typography.displayMedium,
                color = SatWhite,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "اتصال فضائي عالي السرعة بدون انقطاع - اختر باقتك المفضلة",
                style = MaterialTheme.typography.bodyMedium,
                color = SatTextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ================= SECTION 1: المجاني (Gift) =================
        item {
            Text(
                text = "الهدية الترحيبية المجانية 🎁",
                style = MaterialTheme.typography.titleLarge,
                color = SatPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isGlowing = true,
                glowColor = SatPrimary,
                borderWidth = 1.5.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "المجاني 200MB - مجاني لمدة اسبوع",
                                style = MaterialTheme.typography.headlineMedium,
                                color = SatWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isGiftExpired) "انتهت الهدية المجانية" else viewModel.formatRemainingTime(currentUser?.expiryTimestamp ?: 0),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isGiftExpired) SatRed else SatGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SatPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SatPrimary)
                        ) {
                            Text(
                                text = "هدية مجانية",
                                color = SatPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar: Remaining vs Consumed
                    val initialTotal = 200f
                    val currentBalFloat = (currentUser?.balanceMb?.toFloat() ?: 200f).coerceIn(0f, initialTotal)
                    val progressFraction = (currentBalFloat / initialTotal).coerceIn(0f, 1f)

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المتبقي: ${currentUser?.balanceMb ?: 200}MB",
                                color = SatPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "الإجمالي: 200MB",
                                color = SatTextMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SatPrimary,
                            trackColor = SatCardBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onRequestActivateFree() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isConnected) SatPrimary.copy(alpha = 0.8f) else SatPrimary,
                            contentColor = SatBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("activate_free_gift_button")
                    ) {
                        Text(
                            text = if (isConnected) "مفعل مجاني (النت نشط)" else "احصل عليه مجاني (تشغيل النت)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ================= SECTION 2: الباقات الشهرية =================
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "الباقات الشهرية 🗓️",
                style = MaterialTheme.typography.titleLarge,
                color = SatWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "صلاحية كاملة لمدة 30 يوماً مع تجديد السرعة الفضائية",
                style = MaterialTheme.typography.bodySmall,
                color = SatTextMuted
            )
        }

        items(monthlyPackages) { pkg ->
            PackageCardItem(
                pkg = pkg,
                onSubscribe = { selectedPackageForPayment = pkg }
            )
        }

        // ================= SECTION 3: عروض 5 شهور 🔥 =================
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "عروض 5 شهور 🔥",
                    style = MaterialTheme.typography.titleLarge,
                    color = SatGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SatGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SatGold)
                ) {
                    Text(
                        text = "توفير فائق",
                        color = SatGold,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        items(fiveMonthsPackages) { pkg ->
            PackageCardItem(
                pkg = pkg,
                highlight = pkg.isPopular,
                onSubscribe = { selectedPackageForPayment = pkg }
            )
        }

        // ================= SECTION 4: عروض السنة 👑 =================
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "عروض السنة 👑",
                    style = MaterialTheme.typography.titleLarge,
                    color = SatWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SatPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SatPrimary)
                ) {
                    Text(
                        text = "VIP سنوي",
                        color = SatPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        items(yearlyPackages) { pkg ->
            PackageCardItem(
                pkg = pkg,
                onSubscribe = { selectedPackageForPayment = pkg }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Payment & Verification Dialog
    selectedPackageForPayment?.let { pkg ->
        PaymentDialog(
            selectedPackage = pkg,
            onDismiss = { selectedPackageForPayment = null },
            onConfirmActivation = { receiptUri ->
                viewModel.activatePaidPackage(pkg, receiptUri) {
                    selectedPackageForPayment = null
                    showActivationSuccessAlert = true
                }
            }
        )
    }
}

@Composable
fun PackageCardItem(
    pkg: SatellitePackage,
    highlight: Boolean = false,
    onSubscribe: () -> Unit
) {
    val borderColor = if (highlight) SatGold else SatCardBorder

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isGlowing = highlight,
        glowColor = SatGold,
        borderWidth = if (highlight) 1.5.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pkg.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (highlight) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = SatGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${String.format("%,d", pkg.pricePounds)} جنيه سوداني",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (highlight) SatGold else SatPrimary,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = pkg.durationText,
                    style = MaterialTheme.typography.bodySmall,
                    color = SatTextMuted
                )
            }

            Button(
                onClick = onSubscribe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (highlight) SatGold else SatPrimary,
                    contentColor = SatBackground
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(46.dp)
                    .testTag("subscribe_pkg_${pkg.id}")
            ) {
                Text(
                    text = "اشتراك",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
