package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatPrimaryVariant
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite

data class DailyUsagePoint(
    val dayName: String, // e.g. "السبت", "الأحد", ...
    val dateLabel: String,
    val consumedMb: Float,
    val isToday: Boolean = false
)

@Composable
fun DailyUsageChart(
    modifier: Modifier = Modifier,
    dailyPoints: List<DailyUsagePoint>
) {
    var selectedIndex by remember { mutableIntStateOf(dailyPoints.indexOfFirst { it.isToday }.coerceAtLeast(0)) }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val maxConsumption = (dailyPoints.maxOfOrNull { it.consumedMb } ?: 50f).coerceAtLeast(30f)
    val totalWeeklyMb = dailyPoints.sumOf { it.consumedMb.toDouble() }
    val avgDailyMb = if (dailyPoints.isNotEmpty()) totalWeeklyMb / dailyPoints.size else 0.0

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        isGlowing = true,
        glowColor = SatPrimary,
        borderWidth = 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = SatPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "معدل الاستهلاك اليومي",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SatPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SatPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "آخر 7 أيام",
                        color = SatPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "متابعة نمط استهلاك نت قمر X-SAT لتنظيم وتوفير البيانات",
                style = MaterialTheme.typography.bodySmall,
                color = SatTextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Day Banner Details
            val selectedPoint = dailyPoints.getOrNull(selectedIndex) ?: dailyPoints.lastOrNull()
            if (selectedPoint != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SatBackground)
                        .border(1.dp, SatCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedPoint.isToday) SatPrimary else SatGold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${selectedPoint.dayName} (${selectedPoint.dateLabel}):",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SatWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${String.format("%.1f", selectedPoint.consumedMb)} MB",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedPoint.isToday) SatPrimary else SatGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .testTag("daily_usage_chart")
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height - 24.dp.toPx() // leave space for bottom labels
                    val barCount = dailyPoints.size
                    val spacing = canvasWidth / barCount
                    val barWidth = spacing * 0.45f

                    // Grid lines (0%, 50%, 100%)
                    val steps = 3
                    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    for (i in 0..steps) {
                        val y = canvasHeight - (i.toFloat() / steps) * canvasHeight
                        drawLine(
                            color = SatCardBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f,
                            pathEffect = dashPathEffect
                        )
                    }

                    // Draw bars
                    dailyPoints.forEachIndexed { index, point ->
                        val barHeight = ((point.consumedMb / maxConsumption) * canvasHeight * animationProgress.value)
                            .coerceAtLeast(6f)
                        val x = index * spacing + (spacing - barWidth) / 2
                        val y = canvasHeight - barHeight

                        val isSelected = index == selectedIndex
                        val barBrush = Brush.verticalGradient(
                            colors = if (isSelected) {
                                listOf(SatGold, SatPrimary)
                            } else if (point.isToday) {
                                listOf(SatPrimary, SatPrimaryVariant)
                            } else {
                                listOf(SatPrimary.copy(alpha = 0.85f), SatPrimaryVariant.copy(alpha = 0.45f))
                            }
                        )

                        // Bar Shadow/Glow when selected
                        if (isSelected) {
                            drawRoundRect(
                                color = SatPrimary.copy(alpha = 0.3f),
                                topLeft = Offset(x - 3.dp.toPx(), y - 3.dp.toPx()),
                                size = Size(barWidth + 6.dp.toPx(), barHeight + 3.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }

                        // Main Bar
                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }

                // Interactive tap overlay for bars and bottom day labels
                Row(
                    modifier = Modifier.matchParentSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyPoints.forEachIndexed { index, point ->
                        val isSelected = index == selectedIndex
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(160.dp)
                                .clickable { selectedIndex = index },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = "${point.consumedMb.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SatGold else SatTextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = point.dayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SatPrimary else if (point.isToday) SatWhite else SatTextMuted,
                                fontWeight = if (isSelected || point.isToday) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Metrics: Total & Average
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "إجمالي الأسبوع",
                        style = MaterialTheme.typography.bodySmall,
                        color = SatTextMuted
                    )
                    Text(
                        text = "${String.format("%.1f", totalWeeklyMb)} MB",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "المتوسط اليومي",
                        style = MaterialTheme.typography.bodySmall,
                        color = SatTextMuted
                    )
                    Text(
                        text = "${String.format("%.1f", avgDailyMb)} MB/يوم",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "أعلى استهلاك",
                        style = MaterialTheme.typography.bodySmall,
                        color = SatTextMuted
                    )
                    Text(
                        text = "${String.format("%.1f", maxConsumption)} MB",
                        style = MaterialTheme.typography.titleMedium,
                        color = SatGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
