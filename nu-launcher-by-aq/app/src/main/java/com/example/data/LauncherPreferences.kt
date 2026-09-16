package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LauncherThemeMode {
    DARK,      // Liquid Midnight Dark
    OLED,      // Pitch Black OLED for lowest distraction & low light
    DUSK,      // Deep Slate Glass
    LIGHT,     // Frosted Minimal Light
    SYSTEM     // Follow System
}

data class LauncherSettings(
    val themeMode: LauncherThemeMode = LauncherThemeMode.DARK,
    val highContrastLowLight: Boolean = false,
    val clockFormat24h: Boolean = false,
    val showSeconds: Boolean = false,
    val showDate: Boolean = true,
    val showMindfulFocus: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showGesturePill: Boolean = true
)

class LauncherPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nu_launcher_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

    private fun loadSettings(): LauncherSettings {
        val themeString = prefs.getString("theme_mode", LauncherThemeMode.DARK.name) ?: LauncherThemeMode.DARK.name
        val themeMode = try {
            LauncherThemeMode.valueOf(themeString)
        } catch (_: Exception) {
            LauncherThemeMode.DARK
        }

        return LauncherSettings(
            themeMode = themeMode,
            highContrastLowLight = prefs.getBoolean("high_contrast", false),
            clockFormat24h = prefs.getBoolean("clock_24h", false),
            showSeconds = prefs.getBoolean("show_seconds", false),
            showDate = prefs.getBoolean("show_date", true),
            showMindfulFocus = prefs.getBoolean("show_mindful_focus", true),
            hapticsEnabled = prefs.getBoolean("haptics_enabled", true),
            showGesturePill = prefs.getBoolean("show_gesture_pill", true)
        )
    }

    fun setThemeMode(mode: LauncherThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setHighContrastLowLight(enabled: Boolean) {
        prefs.edit().putBoolean("high_contrast", enabled).apply()
        _settings.value = _settings.value.copy(highContrastLowLight = enabled)
    }

    fun setClockFormat24h(enabled: Boolean) {
        prefs.edit().putBoolean("clock_24h", enabled).apply()
        _settings.value = _settings.value.copy(clockFormat24h = enabled)
    }

    fun setShowSeconds(enabled: Boolean) {
        prefs.edit().putBoolean("show_seconds", enabled).apply()
        _settings.value = _settings.value.copy(showSeconds = enabled)
    }

    fun setShowDate(enabled: Boolean) {
        prefs.edit().putBoolean("show_date", enabled).apply()
        _settings.value = _settings.value.copy(showDate = enabled)
    }

    fun setShowMindfulFocus(enabled: Boolean) {
        prefs.edit().putBoolean("show_mindful_focus", enabled).apply()
        _settings.value = _settings.value.copy(showMindfulFocus = enabled)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _settings.value = _settings.value.copy(hapticsEnabled = enabled)
    }

    fun setShowGesturePill(enabled: Boolean) {
        prefs.edit().putBoolean("show_gesture_pill", enabled).apply()
        _settings.value = _settings.value.copy(showGesturePill = enabled)
    }
}
