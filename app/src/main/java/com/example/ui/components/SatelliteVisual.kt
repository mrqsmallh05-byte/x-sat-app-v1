package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatPrimary

@Composable
fun SatelliteVisual(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    isActive: Boolean = false,
    speedMultiplier: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sat_anim")

    // Slow orbital rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (12000 / speedMultiplier).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse radius for satellite waves
    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePulse"
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Radar and beam canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val maxRadius = (this.size.width / 2)

            // Outer orbit line
            drawCircle(
                color = SatPrimary.copy(alpha = 0.2f),
                radius = maxRadius * 0.95f,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Middle orbit line
            drawCircle(
                color = SatPrimary.copy(alpha = 0.15f),
                radius = maxRadius * 0.75f,
                style = Stroke(width = 1.dp.toPx())
            )

            // Glowing waves when active
            if (isActive) {
                drawCircle(
                    color = SatPrimary.copy(alpha = waveAlpha),
                    radius = maxRadius * wavePulse,
                    style = Stroke(width = 2.5.dp.toPx())
                )
                drawCircle(
                    color = SatPrimary.copy(alpha = (waveAlpha * 0.5f)),
                    radius = maxRadius * (wavePulse * 0.7f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }

        // Rotating Satellite Center
        Box(
            modifier = Modifier
                .size(size * 0.65f)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_xsat_logo),
                contentDescription = "قمر X-SAT الصناعي",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            )
        }
    }
}
