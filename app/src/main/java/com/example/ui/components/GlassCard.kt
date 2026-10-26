package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatPrimary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 16.dp,
    isGlowing: Boolean = false,
    glowColor: Color = SatPrimary,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(shapeRadius)
    val borderColor = if (isGlowing) glowColor.copy(alpha = 0.6f) else SatCardBorder

    Surface(
        modifier = modifier
            .clip(shape)
            .shadow(
                elevation = if (isGlowing) 8.dp else 4.dp,
                shape = shape,
                spotColor = if (isGlowing) glowColor else Color.Black
            ),
        shape = shape,
        color = SatCard,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SatCard.copy(alpha = 0.95f),
                            SatCard.copy(alpha = 0.75f)
                        )
                    )
                )
                .padding(16.dp),
            content = content
        )
    }
}
