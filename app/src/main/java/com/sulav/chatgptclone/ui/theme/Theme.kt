package com.sulav.chatgptclone.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF005321),
    onPrimaryContainer = Color(0xFF7DFFB2),
    secondary = SecondaryDark,
    onSecondary = Color(0xFF003355),
    secondaryContainer = Color(0xFF00497B),
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = TertiaryDark,
    onTertiary = Color(0xFF1C1D46),
    tertiaryContainer = Color(0xFF323584),
    onTertiaryContainer = Color(0xFFE0E0FF),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCACACA),
    outline = Color(0xFF8C8C8C),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAFFEF),
    onPrimaryContainer = Color(0xFF002106),
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E0FF),
    onTertiaryContainer = Color(0xFF001158),
    background = Color.White,
    onBackground = BlackText,
    surface = Color.White,
    onSurface = BlackText,
    surfaceVariant = RampingLight,
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFB00020),
    onError = Color.White
)

// Custom color system for gradients and additional colors
@Immutable
data class JumpExtendedColors(
    val backgroundGradient: Brush,
    val rampingBackground: Color,
    val coreBackground: Color,
    val scaleBackground: Color,
    val enterpriseBackground: Color,
    val notStartedBackground: Color,
    val inProgressBackground: Color,
    val completedBackground: Color
)

// Define composition local for our custom color system
val LocalJumpExtendedColors = staticCompositionLocalOf {
    JumpExtendedColors(
        backgroundGradient = JumpGradientBrush,
        rampingBackground = RampingLight,
        coreBackground = CoreGreen,
        scaleBackground = ScaleBlue,
        enterpriseBackground = EnterpriseBlue,
        notStartedBackground = NotStarted,
        inProgressBackground = InProgress,
        completedBackground = Completed
    )
}

@Composable
fun ChatGPTCloneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Create extended colors based on theme
    val jumpExtendedColors = JumpExtendedColors(
        backgroundGradient = if (darkTheme) JumpGradientBrushDark else JumpGradientBrush,
        rampingBackground = if (darkTheme) Color(0xFF1E1E1E) else RampingLight,
        coreBackground = if (darkTheme) CoreGreen.copy(alpha = 0.9f) else CoreGreen,
        scaleBackground = if (darkTheme) ScaleBlue.copy(alpha = 0.8f) else ScaleBlue,
        enterpriseBackground = if (darkTheme) EnterpriseBlue.copy(alpha = 0.9f) else EnterpriseBlue,
        notStartedBackground = if (darkTheme) NotStarted.copy(alpha = 0.8f) else NotStarted,
        inProgressBackground = if (darkTheme) InProgress.copy(alpha = 0.8f) else InProgress,
        completedBackground = if (darkTheme) Completed.copy(alpha = 0.8f) else Completed
    )

    // Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalJumpExtendedColors provides jumpExtendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

// Extension property to access JumpExtendedColors
val MaterialTheme.jumpColors: JumpExtendedColors
    @Composable
    get() = LocalJumpExtendedColors.current