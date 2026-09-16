package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LauncherPreferences
import com.example.ui.gestures.QuickGesturesSheet
import com.example.ui.home.HomeScreen
import com.example.ui.settings.SettingsSheet
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferences: LauncherPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = LauncherPreferences(applicationContext)

        setContent {
            val settings by preferences.settings.collectAsStateWithLifecycle()

            MyApplicationTheme(
                themeMode = settings.themeMode,
                highContrast = settings.highContrastLowLight
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LauncherApp(preferences = preferences)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherApp(preferences: LauncherPreferences) {
    val settings by preferences.settings.collectAsStateWithLifecycle()
    var showGesturesSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Intercept back button to dismiss any open sheet
    BackHandler(enabled = showGesturesSheet || showSettingsSheet) {
        if (showGesturesSheet) showGesturesSheet = false
        if (showSettingsSheet) showSettingsSheet = false
    }

    HomeScreen(
        settings = settings,
        preferences = preferences,
        onOpenGestures = { showGesturesSheet = true },
        onOpenSettings = { showSettingsSheet = true }
    )

    if (showGesturesSheet) {
        QuickGesturesSheet(
            onDismiss = { showGesturesSheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsSheet(
            settings = settings,
            preferences = preferences,
            onDismiss = { showSettingsSheet = false }
        )
    }
}
