package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.GlassCard
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite

@Composable
fun TermsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("سياسة الخصوصية", "شروط وأحكام الحكيم")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatBackground)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "سياسة الخصوصية والشروط",
            style = MaterialTheme.typography.displayMedium,
            color = SatWhite,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "اتفاقية الاستخدام وحماية البيانات الخاصة بمشتركي قمر X-SAT",
            style = MaterialTheme.typography.bodyMedium,
            color = SatTextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SatCard,
            contentColor = SatPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = SatPrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) SatPrimary else SatTextMuted
                        )
                    },
                    modifier = Modifier.testTag("terms_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (selectedTab == 0) {
                PrivacyPolicyContent()
            } else {
                TermsConditionsContent()
            }
        }
    }
}

@Composable
private fun PrivacyPolicyContent() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "سياسة الخصوصية وأمن المشترك",
                style = MaterialTheme.typography.titleLarge,
                color = SatPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            val points = listOf(
                "1. نحن X-SAT لا نجمع موقعك، لا نقرأ رسائلك، لا نصل لصورك.",
                "2. البيانات التي نجمعها فقط: الاسم، رقم الواتساب، المنطقة، نوع الجهاز، استهلاك الانترنت داخل التطبيق فقط.",
                "3. بياناتك محفوظة داخل تلفونك فقط (Room) ولا ترسل لأي خادم خارجي.",
                "4. لن نشارك رقمك مع أي جهة.",
                "5. يمكنك حذف حسابك وبياناتك نهائياً من الإعدادات.",
                "6. التطبيق يستخدم VPN حقيقي فقط لتوفير الانترنت ولا يتجسس."
            )

            points.forEach { point ->
                Text(
                    text = point,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SatWhite,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "X-SAT لا يجمع بياناتك. مفاتيح القمر مربوطة داخل التطبيق. نعد فقط الاستهلاك. الهدية 200MB صالحة 7 ايام فقط.",
                style = MaterialTheme.typography.bodyMedium,
                color = SatGold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TermsConditionsContent() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        isGlowing = true,
        glowColor = SatGold
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "شروط استخدام X-SAT - يمنع مخالفتها",
                style = MaterialTheme.typography.titleLarge,
                color = SatGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            val rules = listOf(
                "1. الهدية المجانية 200MB لمرة واحدة فقط لكل رقم واتساب - يمنع انشاء حسابات وهمية - سيتم حظر الارقام المكررة نهائيا.",
                "2. يمنع بيع او مشاركة حسابك مع شخص اخر - الحساب مرتبط بجهاز واحد فقط.",
                "3. يمنع استخدام الانترنت لاغراض غير قانونية (اختراق، نصب، محتوى اباحي للاطفال) - سيتم حظر الحساب فورا والابلاغ.",
                "4. باقات X-SAT مدفوعة وغير قابلة للاسترجاع بعد التفعيل - تأكد قبل الدفع.",
                "5. السرعة تعتمد على تغطية القمر في منطقتك - لا نضمن سرعة ثابتة في كل الاوقات - قد تضعف في الطقس السيء.",
                "6. يمنع فك شفرة التطبيق او استخراج مفاتيح القمر - مفاتيح القمر ملك للشركة ومربوطة داخل التطبيق.",
                "7. في حال انتهاك الشروط، يحق لـ X-SAT ايقاف الخدمة بدون تعويض وحظر الجهاز نهائيا.",
                "8. الاسعار قابلة للتغيير - سيتم اعلامك قبل 3 ايام.",
                "9. بالتسجيل انت توافق ان عمرك فوق 18 سنة وانت المسؤول عن استخدامك.",
                "10. الدعم الفني عبر واتساب فقط - لا يوجد دعم هاتفي."
            )

            rules.forEach { rule ->
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SatWhite,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "بالضغط على انشاء حساب انت وافقت على كل الشروط اعلاه.",
                style = MaterialTheme.typography.titleMedium,
                color = SatPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
