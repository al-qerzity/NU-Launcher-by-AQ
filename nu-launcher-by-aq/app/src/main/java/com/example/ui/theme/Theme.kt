package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.LauncherThemeMode

private val LiquidDarkColorScheme = darkColorScheme(
    primary = GlassWhiteHigh,
    onPrimary = Color.Black,
    surface = LiquidGlassSurface,
    onSurface = GlassWhiteHigh,
    background = LiquidDarkBg,
    onBackground = GlassWhiteHigh,
    secondary = LiquidAccentCyan,
    onSecondary = Color.Black,
    surfaceVariant = LiquidGlassSurfaceElevated,
    onSurfaceVariant = GlassWhiteMedium
)

private val LiquidOledColorScheme = darkColorScheme(
    primary = GlassWhiteHigh,
    onPrimary = Color.Black,
    surface = Color(0x12FFFFFF),
    onSurface = GlassWhiteHigh,
    background = LiquidOledBg,
    onBackground = GlassWhiteHigh,
    secondary = GlassWhiteHigh,
    onSecondary = Color.Black,
    surfaceVariant = Color(0x20FFFFFF),
    onSurfaceVariant = GlassWhiteMedium
)

private val LiquidDuskColorScheme = darkColorScheme(
    primary = GlassWhiteHigh,
    onPrimary = Color.Black,
    surface = Color(0x202B3A4F),
    onSurface = GlassWhiteHigh,
    background = LiquidDuskBg,
    onBackground = GlassWhiteHigh,
    secondary = LiquidAccentCyan,
    onSecondary = Color.Black,
    surfaceVariant = Color(0x35354964),
    onSurfaceVariant = GlassWhiteMedium
)

private val LiquidLightColorScheme = lightColorScheme(
    primary = Color(0xFF1C1D22),
    onPrimary = Color.White,
    surface = Color(0xCCFFFFFF),
    onSurface = Color(0xFF1C1D22),
    background = LiquidLightBg,
    onBackground = Color(0xFF1C1D22),
    secondary = Color(0xFF007AFF),
    onSecondary = Color.White,
    surfaceVariant = Color(0xE6FFFFFF),
    onSurfaceVariant = Color(0xFF484A54)
)

@Composable
fun MyApplicationTheme(
    themeMode: LauncherThemeMode = LauncherThemeMode.DARK,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        LauncherThemeMode.DARK -> true
        LauncherThemeMode.OLED -> true
        LauncherThemeMode.DUSK -> true
        LauncherThemeMode.LIGHT -> false
        LauncherThemeMode.SYSTEM -> systemDark
    }

    val baseScheme = when (themeMode) {
        LauncherThemeMode.OLED -> LiquidOledColorScheme
        LauncherThemeMode.DUSK -> LiquidDuskColorScheme
        LauncherThemeMode.LIGHT -> LiquidLightColorScheme
        LauncherThemeMode.DARK -> LiquidDarkColorScheme
        LauncherThemeMode.SYSTEM -> if (systemDark) LiquidDarkColorScheme else LiquidLightColorScheme
    }

    val finalScheme = if (highContrast && isDark) {
        baseScheme.copy(
            onSurface = Color.White,
            onBackground = Color.White,
            surface = Color(0x2EFFFFFF)
        )
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}
