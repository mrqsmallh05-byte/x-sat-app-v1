package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatBlue
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite
import com.example.viewmodel.SatellitePackage
import kotlinx.coroutines.delay

@Composable
fun PaymentDialog(
    selectedPackage: SatellitePackage,
    onDismiss: () -> Unit,
    onConfirmActivation: (receiptUri: String?) -> Unit
) {
    var hasUploadedReceipt by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(5) }
    var isSuccess by remember { mutableStateOf(false) }
    var copiedAccountNotice by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(isVerifying) {
        if (isVerifying) {
            for (i in 5 downTo 1) {
                countdownSeconds = i
                delay(1000)
            }
            isVerifying = false
            isSuccess = true
            delay(1500)
            onConfirmActivation("receipt_sample_${System.currentTimeMillis()}.png")
        }
    }

    Dialog(onDismissRequest = {
        if (!isVerifying) onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SatCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SatPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Bankak",
                        tint = SatGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الدفع عبر بنكك (بنك الخرطوم)",
                        style = MaterialTheme.typography.titleLarge,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Package brief
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SatBackground)
                        .border(1.dp, SatCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "باقة: ${selectedPackage.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "المبلغ المطلوب: ${String.format("%,d", selectedPackage.pricePounds)} جنيه سوداني",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SatGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transfer destination instructions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SatBlue.copy(alpha = 0.15f))
                        .border(1.dp, SatBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "حول المبلغ بنكك على:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SatTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "1234567",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = SatWhite,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "اسم الحساب: X-SAT",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SatPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("1234567"))
                                    copiedAccountNotice = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ رقم الحساب",
                                    tint = SatPrimary
                                )
                            }
                        }
                        if (copiedAccountNotice) {
                            Text(
                                text = "تم نسخ رقم الحساب بنجاح!",
                                style = MaterialTheme.typography.labelMedium,
                                color = SatPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Verification and Upload state
                if (isSuccess) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "نجاح",
                            tint = SatPrimary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "تم التأكيد - تم تفعيل الباقة بنجاح!",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "تمت إضافة الرصيد وتمديد فترة الصلاحية",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SatWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (isVerifying) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SatPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جاري مراجعة إشعار بنكك والتحقق من المعاملة...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SatWhite,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "المتبقي: $countdownSeconds ثوانٍ",
                            style = MaterialTheme.typography.titleMedium,
                            color = SatGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Upload receipt button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SatBackground)
                            .border(
                                1.dp,
                                if (hasUploadedReceipt) SatPrimary else SatCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                hasUploadedReceipt = true
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (hasUploadedReceipt) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = "رفع الإشعار",
                                tint = if (hasUploadedReceipt) SatPrimary else SatGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasUploadedReceipt) "تم اختيار إشعار بنكك (جاهز)" else "رفع إشعار بنكك (صورة المعاملة)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (hasUploadedReceipt) SatPrimary else SatWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء", color = SatTextMuted)
                        }

                        Button(
                            onClick = {
                                if (!hasUploadedReceipt) {
                                    hasUploadedReceipt = true
                                }
                                isVerifying = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SatPrimary,
                                contentColor = SatBackground
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("confirm_payment_button")
                        ) {
                            Text(
                                text = "تأكيد الاشتراك",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
