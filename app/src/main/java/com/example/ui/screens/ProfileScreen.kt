package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.theme.AppThemeMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatRed
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite
import com.example.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Profile Avatar Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(SatPrimary.copy(alpha = 0.15f))
                .border(2.dp, SatPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = SatPrimary,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = currentUser?.fullName ?: "المشترك",
            style = MaterialTheme.typography.headlineLarge,
            color = SatWhite,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = currentUser?.phone ?: "09xxxxxxxx",
            style = MaterialTheme.typography.titleMedium,
            color = SatPrimary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // User Data Glass Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "بيانات الاشتراك والجهاز",
                    style = MaterialTheme.typography.titleMedium,
                    color = SatGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileDataRow(label = "المنطقة:", value = currentUser?.region ?: "ابوحمد")
                ProfileDataRow(label = "نوع الجهاز:", value = currentUser?.phoneModel ?: "Huawei")
                ProfileDataRow(label = "الباقة الحالية:", value = currentUser?.activePackageName ?: "المجاني 200MB")
                ProfileDataRow(label = "الرصيد المتبقي:", value = "${currentUser?.balanceMb ?: 0} MB")
                ProfileDataRow(label = "فترة الصلاحية:", value = viewModel.formatRemainingTime(currentUser?.expiryTimestamp ?: 0))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pre-bound LPA Satellite Keys Section
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = SatPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مفاتيح مشغلي القمر الصناعي (LPA)",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "تم ربط 7 مفاتيح أقمار صناعية مباشرة داخل بنية التطبيق لتوفير إنترنت فوري دون حاجة المستخدم لإدخال أي مفاتيح:",
                    style = MaterialTheme.typography.bodySmall,
                    color = SatTextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                val carriers = listOf(
                    "Monogoto Satellite",
                    "Soracom Orbital",
                    "Sateliot LEO Sudan",
                    "eSIM-Go Orbit",
                    "Airalo Global Sat",
                    "Globalyo Beam",
                    "Keepgo Geostationary"
                )

                carriers.forEach { carrier ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• $carrier",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SatWhite
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SatPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "مربوط جاهز",
                                color = SatPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Theme & Battery Saver Mode Card
        val currentThemeMode by viewModel.currentThemeMode.collectAsState()

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isGlowing = currentThemeMode == AppThemeMode.AMOLED_BATTERY_SAVER,
            glowColor = SatPrimary
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Brightness4,
                        contentDescription = null,
                        tint = SatPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "نظام المظهر والسمات (Theme)",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "دعم الوضع الداكن وتوفير طاقة البطارية عبر تقليل سطوع الشاشة وإطفاء بكسلات OLED.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SatTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                val themeOptions = listOf(
                    Triple(AppThemeMode.SPACE_DARK, "الوضع الداكن الفضائي", "السمة الأساسية المظلمة مريحة للعين"),
                    Triple(AppThemeMode.AMOLED_BATTERY_SAVER, "توفير البطارية (AMOLED)", "أسود نقي #000000 يطفئ بكسلات OLED تماماً"),
                    Triple(AppThemeMode.SYSTEM, "تلقائي حسب نظام الجهاز", "يتوافق تلقائياً مع سمة هاتف أندرويد"),
                    Triple(AppThemeMode.OUTDOOR_LIGHT, "الوضع النهاري عالي الوضوح", "للاستخدام تحت أشعة الشمس المباشرة")
                )

                themeOptions.forEach { (mode, label, desc) ->
                    val isSelected = currentThemeMode == mode
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SatPrimary.copy(alpha = 0.15f) else SatBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) SatPrimary else SatCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.setThemeMode(mode) }
                            .testTag("theme_option_${mode.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) SatPrimary else SatWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SatTextMuted
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "محدد",
                                    tint = SatPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = SatRed.copy(alpha = 0.2f),
                contentColor = SatRed
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = SatRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "تسجيل الخروج",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = SatTextMuted, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = SatWhite, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
