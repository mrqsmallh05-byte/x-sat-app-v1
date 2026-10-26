package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Space Dark Theme (Default)
val SatPrimary = Color(0xFF00FF88)        // Glowing Neon Green
val SatPrimaryVariant = Color(0xFF00CC6A) // Deep Neon Green
val SatBackground = Color(0xFF0A0E1A)    // Deep Space Dark Blue/Black
val SatSurface = Color(0xFF13192B)       // Elevated Space Navy
val SatCard = Color(0xFF1A1F2E)          // Glass Card Surface
val SatCardBorder = Color(0xFF28324E)    // Card Border
val SatWhite = Color(0xFFFFFFFF)
val SatTextMuted = Color(0xFF8E9BAE)
val SatRed = Color(0xFFFF4444)           // Disconnected / Alert
val SatGold = Color(0xFFFFD166)          // Premium / Specials
val SatBlue = Color(0xFF2196F3)          // Bank of Khartoum accent

// AMOLED True Black (Ultra Battery Saver)
val AmoledBackground = Color(0xFF000000) // True Pitch Black (0% OLED Power)
val AmoledSurface = Color(0xFF080C14)
val AmoledCard = Color(0xFF0D121F)
val AmoledCardBorder = Color(0xFF1A233A)

// Outdoor High Contrast Daytime Theme
val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFFFFFFF)
val LightCard = Color(0xFFFFFFFF)
val LightCardBorder = Color(0xFFCBD5E1)
val LightPrimary = Color(0xFF00A859)     // Deep Emerald Satellite Green
val LightTextPrimary = Color(0xFF0F172A)
val LightTextMuted = Color(0xFF64748B)

enum class AppThemeMode {
    SPACE_DARK,             // الوضع الداكن الفضائي
    AMOLED_BATTERY_SAVER,   // وضع توفير البطارية (AMOLED أسود نقي)
    SYSTEM,                 // تلقائي حسب النظام
    OUTDOOR_LIGHT           // الوضع النهاري عالي الوضوح
}
