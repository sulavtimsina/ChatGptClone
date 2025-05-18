package com.sulav.chatgptclone.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core brand colors from Jump
val JumpGreen = Color(0xFF00C853)
val JumpLightGreen = Color(0xFF17D264)
val JumpBlue = Color(0xFF0288D1)
val JumpDarkBlue = Color(0xFF006DB3)
val JumpGradientStart = Color(0xFF00C853)
val JumpGradientMiddle = Color(0xFF06A7B0)
val JumpGradientEnd = Color(0xFF0288D1)

// UI element colors
val RampingLight = Color(0xFFF5F7F9)
val CoreGreen = Color(0xFF17D264)
val ScaleBlue = Color(0xFFD6EFFF)
val EnterpriseBlue = Color(0xFF3F51B5)
val BlackText = Color(0xFF121212)
val GrayText = Color(0xFF6C757D)

// Status colors
val NotStarted = Color(0xFF6C757D)
val InProgress = Color(0xFFFFC107)
val Completed = Color(0xFF28A745)

// Light theme colors (replacing your Purple40, etc.)
val Primary = JumpGreen
val Secondary = JumpBlue
val Tertiary = EnterpriseBlue

// Dark theme colors (replacing your Purple80, etc.)
val PrimaryDark = JumpLightGreen
val SecondaryDark = Color(0xFF9ECAFF) // Lighter blue for dark theme
val TertiaryDark = Color(0xFFBBC2FF) // Lighter enterprise blue for dark theme

// Create gradient brush
val JumpGradientBrush = Brush.horizontalGradient(
    colors = listOf(JumpGradientStart, JumpGradientMiddle, JumpGradientEnd)
)

val JumpGradientBrushDark = Brush.horizontalGradient(
    colors = listOf(
        JumpGradientStart.copy(alpha = 0.8f),
        JumpGradientMiddle.copy(alpha = 0.8f),
        JumpGradientEnd.copy(alpha = 0.8f)
    )
)