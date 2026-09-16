package com.example.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryStd
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LauncherPreferences
import com.example.data.LauncherSettings
import com.example.model.EssentialAppInfo
import com.example.ui.components.EssentialAppGlassCard
import com.example.ui.components.LiquidGlassBackground
import com.example.ui.components.LiquidGlassCard
import com.example.util.LauncherUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MINDFUL_QUOTES = listOf(
    "Focus on what truly matters.",
    "Digital silence brings clarity.",
    "Disconnect to reconnect.",
    "Less noise, more life.",
    "Simplicity is the ultimate sophistication.",
    "Be present in this moment.",
    "Your attention is your greatest asset."
)

@Composable
fun HomeScreen(
    settings: LauncherSettings,
    preferences: LauncherPreferences,
    onOpenGestures: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(Date()) }
    var quoteIndex by remember { mutableIntStateOf(0) }
    var apps by remember { mutableStateOf<List<EssentialAppInfo>>(emptyList()) }
    val isDeviceAdminActive = remember { LauncherUtils.isDeviceAdminActive(context) }

    // Live clock timer update
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    // Refresh installed essential apps
    LaunchedEffect(Unit) {
        apps = LauncherUtils.getEssentialApps(context)
    }

    val timeFormatPattern = if (settings.clockFormat24h) "HH:mm" else "hh:mm"
    val timeFormatter = remember(settings.clockFormat24h) {
        SimpleDateFormat(timeFormatPattern, Locale.getDefault())
    }
    val amPmFormatter = remember { SimpleDateFormat("a", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    LiquidGlassBackground(
        themeMode = settings.themeMode,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Double tap gesture opens Quick Controls
                detectTapGestures(
                    onDoubleTap = {
                        LauncherUtils.performHaptic(context)
                        onOpenGestures()
                    }
                )
            }
            .pointerInput(Unit) {
                // Vertical drag gesture: swipe up for Quick Gestures, swipe down for Notifications
                var dragAmount = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragAmount = 0f },
                    onDragEnd = {
                        if (dragAmount < -80f) {
                            // Swiped up
                            LauncherUtils.performHaptic(context)
                            onOpenGestures()
                        } else if (dragAmount > 120f) {
                            // Swiped down
                            LauncherUtils.expandNotificationShade(context)
                        }
                    },
                    onVerticalDrag = { _, drag ->
                        dragAmount += drag
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Status Capsule & Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status indicator pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDeviceAdminActive) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = "Uninstall Protection Active",
                            tint = Color(0xFF30D158),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = "NU LAUNCHER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Top Actions: Quick Gestures & Settings
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Quick Gestures Panel button
                    IconButton(
                        onClick = {
                            LauncherUtils.performHaptic(context)
                            onOpenGestures()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), CircleShape)
                            .testTag("quick_gestures_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Quick Controls",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings button
                    IconButton(
                        onClick = {
                            LauncherUtils.performHaptic(context)
                            onOpenSettings()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)), CircleShape)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Launcher Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // MIDDLE SECTION: MINIMALIST LIQUID CLOCK & FOCUS CARD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Large Digital Clock
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormatter.format(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.ExtraLight,
                            letterSpacing = (-2).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (!settings.clockFormat24h) {
                        Text(
                            text = " " + amPmFormatter.format(currentTime).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                // Date
                if (settings.showDate) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormatter.format(currentTime),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mindful Focus Quote Card
                if (settings.showMindfulFocus) {
                    Spacer(modifier = Modifier.height(22.dp))
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(20.dp)),
                        backgroundColor = Color.White.copy(alpha = 0.05f),
                        borderColor = Color.White.copy(alpha = 0.12f),
                        onClick = {
                            LauncherUtils.performHaptic(context)
                            quoteIndex = (quoteIndex + 1) % MINDFUL_QUOTES.size
                        }
                    ) {
                        Text(
                            text = "“${MINDFUL_QUOTES[quoteIndex]}”",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }

            // BOTTOM SECTION: ESSENTIAL APPS (Phone, Messaging, WhatsApp) & GESTURE BAR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Section Title (Minimalist)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ESSENTIAL APPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "Distraction-Free",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                // Essential App Cards (Phone, Messages, WhatsApp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    apps.forEach { app ->
                        EssentialAppGlassCard(
                            app = app,
                            onClick = {
                                LauncherUtils.launchEssentialApp(context, app)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Floating Quick Controls Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(
                            BorderStroke(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.White.copy(alpha = 0.08f)
                                    )
                                )
                            ),
                            RoundedCornerShape(50)
                        )
                        .clickable {
                            LauncherUtils.performHaptic(context)
                            onOpenGestures()
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("gestures_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription = "Swipe up for controls",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Swipe up for controls",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
