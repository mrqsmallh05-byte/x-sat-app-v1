package com.example.ui.screens

import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ui.components.GlassCard
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatRed
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite

@Composable
fun RegisterScreen(
    onRegisterClick: (
        fullName: String,
        phone: String,
        region: String,
        phoneModel: String,
        pass: String,
        confirmPass: String,
        agreed: Boolean
    ) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("ابوحمد") }
    var regionDropdownExpanded by remember { mutableStateOf(false) }

    val defaultModel = remember {
        val model = Build.MODEL
        val manu = Build.MANUFACTURER
        if (model.startsWith(manu, ignoreCase = true)) model else "$manu $model"
    }
    var phoneModel by remember { mutableStateOf(defaultModel) }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    val regions = listOf(
        "ابوحمد",
        "مقرات",
        "ام عود",
        "السرسف",
        "جزيرة مقرات",
        "بربر",
        "عطبرة",
        "اخرى"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "انشاء حساب جديد",
                style = MaterialTheme.typography.displayMedium,
                color = SatPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "سجل الآن واحصل فوراً على 200MB مجانية صالحة لمدة أسبوع",
                style = MaterialTheme.typography.bodyMedium,
                color = SatGold,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isGlowing = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SatRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = errorMessage,
                                color = SatRed,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 1. Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("الاسم الكامل (مثال: محمد أحمد)") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SatPrimary)
                        },
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_fullname_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. WhatsApp Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 10) phone = it },
                        label = { Text("رقم الواتساب (0912345678)") },
                        placeholder = { Text("رقم سوداني فريد - هدية واحدة لكل رقم") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SatPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Region Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedRegion,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("المنطقة") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SatPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { regionDropdownExpanded = true }) {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "قائمة المناطق",
                                        tint = SatPrimary
                                    )
                                }
                            },
                            colors = outlinedFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { regionDropdownExpanded = true }
                                .testTag("register_region_dropdown")
                        )

                        DropdownMenu(
                            expanded = regionDropdownExpanded,
                            onDismissRequest = { regionDropdownExpanded = false },
                            modifier = Modifier.background(SatCard)
                        ) {
                            regions.forEach { reg ->
                                DropdownMenuItem(
                                    text = { Text(reg, color = SatWhite, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        selectedRegion = reg
                                        regionDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Phone Model
                    OutlinedTextField(
                        value = phoneModel,
                        onValueChange = { phoneModel = it },
                        label = { Text("نوع التلفون (مثال: Huawei Y9a)") },
                        leadingIcon = {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SatPrimary)
                        },
                        singleLine = true,
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone_model_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور (6 أحرف على الأقل)") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SatPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "تبديل الرؤية",
                                    tint = SatTextMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6. Confirm Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("تأكيد كلمة المرور") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SatPrimary)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = outlinedFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_confirm_password_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Terms agreement checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { agreedToTerms = !agreedToTerms }
                    ) {
                        Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SatPrimary,
                                checkmarkColor = SatBackground,
                                uncheckedColor = SatCardBorder
                            ),
                            modifier = Modifier.testTag("terms_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "أوافق على سياسة الخصوصية وشروط الاستخدام",
                                color = SatWhite,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "اضغط هنا لقراءة شروط الحكيم والخصوصية كاملة",
                                color = SatGold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.clickable { onNavigateToTerms() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit button
                    Button(
                        onClick = {
                            onRegisterClick(
                                fullName,
                                phone,
                                selectedRegion,
                                phoneModel,
                                password,
                                confirmPassword,
                                agreedToTerms
                            )
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SatPrimary,
                            contentColor = SatBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("register_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = SatBackground,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "انشاء حساب + احصل على 200MB مجاني",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Already have an account link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLogin() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "لديك حساب بالفعل؟ ",
                            color = SatTextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "تسجيل الدخول",
                            color = SatPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SatPrimary,
    unfocusedBorderColor = SatCardBorder,
    focusedTextColor = SatWhite,
    unfocusedTextColor = SatWhite,
    focusedLabelColor = SatPrimary,
    unfocusedLabelColor = SatTextMuted
)
