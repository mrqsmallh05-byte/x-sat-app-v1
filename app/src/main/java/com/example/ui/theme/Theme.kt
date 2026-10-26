package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Space Dark Theme (Default)
private val SpaceDarkColorScheme = darkColorScheme(
    primary = SatPrimary,
    onPrimary = SatBackground,
    primaryContainer = SatCard,
    onPrimaryContainer = SatPrimary,
    secondary = SatGold,
    onSecondary = SatBackground,
    background = SatBackground,
    onBackground = SatWhite,
    surface = SatSurface,
    onSurface = SatWhite,
    surfaceVariant = SatCard,
    onSurfaceVariant = SatTextMuted,
    outline = SatCardBorder,
    error = SatRed,
    onError = SatWhite
)

// AMOLED Ultra Battery Saver Color Scheme (True #000000)
private val AmoledBatterySaverColorScheme = darkColorScheme(
    primary = SatPrimary,
    onPrimary = AmoledBackground,
    primaryContainer = AmoledCard,
    onPrimaryContainer = SatPrimary,
    secondary = SatGold,
    onSecondary = AmoledBackground,
    background = AmoledBackground,
    onBackground = SatWhite,
    surface = AmoledSurface,
    onSurface = SatWhite,
    surfaceVariant = AmoledCard,
    onSurfaceVariant = SatTextMuted,
    outline = AmoledCardBorder,
    error = SatRed,
    onError = SatWhite
)

// Outdoor Daytime High-Contrast Color Scheme
private val OutdoorLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = SatWhite,
    primaryContainer = LightCard,
    onPrimaryContainer = LightPrimary,
    secondary = SatGold,
    onSecondary = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextMuted,
    outline = LightCardBorder,
    error = SatRed,
    onError = SatWhite
)

@Composable
fun XSatTheme(
    themeMode: AppThemeMode = AppThemeMode.SPACE_DARK,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()

    val colorScheme = when (themeMode) {
        AppThemeMode.SPACE_DARK -> SpaceDarkColorScheme
        AppThemeMode.AMOLED_BATTERY_SAVER -> AmoledBatterySaverColorScheme
        AppThemeMode.OUTDOOR_LIGHT -> OutdoorLightColorScheme
        AppThemeMode.SYSTEM -> if (systemIsDark) SpaceDarkColorScheme else OutdoorLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
