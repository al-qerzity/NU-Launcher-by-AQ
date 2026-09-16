package com.example.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LauncherPreferences
import com.example.data.LauncherSettings
import com.example.data.LauncherThemeMode
import com.example.util.LauncherUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: LauncherSettings,
    preferences: LauncherPreferences,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    var isDeviceAdminActive by remember { mutableStateOf(LauncherUtils.isDeviceAdminActive(context)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F121A).copy(alpha = 0.96f),
        scrimColor = Color.Black.copy(alpha = 0.70f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        },
        modifier = Modifier.testTag("settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 44.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Launcher Settings",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Customize minimalism, theme & protection",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close Settings",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 1: UNINSTALL PROTECTION (NON-UNINSTALLABLE)
            SettingsSectionHeader(title = "Security & Protection", icon = Icons.Rounded.Security)
            Spacer(modifier = Modifier.height(8.dp))

            val adminBorderColor = if (isDeviceAdminActive) Color(0xFF30D158) else Color(0xFFFF9F0A)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(
                        BorderStroke(1.2.dp, adminBorderColor.copy(alpha = 0.5f)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(adminBorderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDeviceAdminActive) Icons.Rounded.Shield else Icons.Rounded.Lock,
                                    contentDescription = null,
                                    tint = adminBorderColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Non-Uninstallable Mode",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isDeviceAdminActive) "Active Protection" else "Inactive",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = adminBorderColor
                                )
                            }
                        }

                        Switch(
                            checked = isDeviceAdminActive,
                            onCheckedChange = { enable ->
                                LauncherUtils.performHaptic(context)
                                if (enable) {
                                    context.startActivity(LauncherUtils.buildEnableDeviceAdminIntent(context))
                                } else {
                                    val success = LauncherUtils.removeDeviceAdmin(context)
                                    if (success) {
                                        isDeviceAdminActive = false
                                        Toast.makeText(context, "Protection disabled", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Deactivate in Device Administrators", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF30D158),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.testTag("uninstall_protection_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isDeviceAdminActive) {
                            "Device Administrator is ACTIVE. Android system prevents this app from being uninstalled via settings or home screens. It cannot be uninstalled unless this admin is deactivated or the device undergoes a factory reset."
                        } else {
                            "Enable to make NU Launcher non-uninstallable. Android will register this app as a Device Administrator, blocking standard uninstallation unless deactivated or factory reset."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 17.sp),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 2: DARK MODE & LOW LIGHT READABILITY
            SettingsSectionHeader(title = "Appearance & Low-Light Mode", icon = Icons.Rounded.DarkMode)
            Spacer(modifier = Modifier.height(8.dp))

            // Theme options grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(20.dp))
                    .padding(8.dp)
            ) {
                ThemeOptionItem(
                    title = "Liquid Dark",
                    subtitle = "Deep midnight liquid glass",
                    isSelected = settings.themeMode == LauncherThemeMode.DARK,
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        preferences.setThemeMode(LauncherThemeMode.DARK)
                    }
                )
                ThemeOptionItem(
                    title = "Pitch Black OLED",
                    subtitle = "Pure black #000000 for low light & battery saving",
                    isSelected = settings.themeMode == LauncherThemeMode.OLED,
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        preferences.setThemeMode(LauncherThemeMode.OLED)
                    }
                )
                ThemeOptionItem(
                    title = "Liquid Dusk",
                    subtitle = "Subtle slate & navy glass",
                    isSelected = settings.themeMode == LauncherThemeMode.DUSK,
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        preferences.setThemeMode(LauncherThemeMode.DUSK)
                    }
                )
                ThemeOptionItem(
                    title = "Frosted Light",
                    subtitle = "Pristine white frosted glass",
                    isSelected = settings.themeMode == LauncherThemeMode.LIGHT,
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        preferences.setThemeMode(LauncherThemeMode.LIGHT)
                    }
                )
                ThemeOptionItem(
                    title = "Follow System",
                    subtitle = "Syncs with system dark/light schedule",
                    isSelected = settings.themeMode == LauncherThemeMode.SYSTEM,
                    onClick = {
                        LauncherUtils.performHaptic(context)
                        preferences.setThemeMode(LauncherThemeMode.SYSTEM)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // High Contrast for Low Light
            SettingsToggleRow(
                icon = Icons.Rounded.Visibility,
                title = "High Contrast for Low Light",
                subtitle = "Boosts text sharpness & border contrast in dark environments",
                isChecked = settings.highContrastLowLight,
                onCheckedChange = {
                    LauncherUtils.performHaptic(context)
                    preferences.setHighContrastLowLight(it)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 3: MINIMALIST DISPLAY & GESTURES
            SettingsSectionHeader(title = "Display & Gestures", icon = Icons.Rounded.Schedule)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(20.dp))
                    .padding(8.dp)
            ) {
                SettingsToggleItem(
                    icon = Icons.Rounded.Schedule,
                    title = "24-Hour Clock",
                    subtitle = "Display time in 24-hour military format",
                    isChecked = settings.clockFormat24h,
                    onCheckedChange = {
                        LauncherUtils.performHaptic(context)
                        preferences.setClockFormat24h(it)
                    }
                )

                SettingsToggleItem(
                    icon = Icons.Rounded.FormatQuote,
                    title = "Mindful Focus Quote",
                    subtitle = "Distraction-free intention prompt on home screen",
                    isChecked = settings.showMindfulFocus,
                    onCheckedChange = {
                        LauncherUtils.performHaptic(context)
                        preferences.setShowMindfulFocus(it)
                    }
                )

                SettingsToggleItem(
                    icon = Icons.Rounded.Vibration,
                    title = "Tactile Haptics",
                    subtitle = "Subtle vibration clicks on glass interaction",
                    isChecked = settings.hapticsEnabled,
                    onCheckedChange = {
                        LauncherUtils.performHaptic(context)
                        preferences.setHapticsEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 4: DEFAULT LAUNCHER SHORTCUT
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF64D2FF).copy(alpha = 0.20f),
                                Color(0xFFBF5AF2).copy(alpha = 0.15f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, Color(0xFF64D2FF).copy(alpha = 0.4f)),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        LauncherUtils.openDefaultAppsSettings(context)
                    }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Set as Default Home App",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                        Text(
                            text = "Tap to open system dialog and choose NU Launcher",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 5: ABOUT & PRIVACY GUARANTEE
            SettingsSectionHeader(title = "About NU Launcher by AQ", icon = Icons.Rounded.Info)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NU Launcher by AQ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v1.0",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Package: com.alqerzity.nulauncher",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "100% Offline & Private. This application contains zero internet permissions, zero telemetry, and zero tracking. Designed strictly for distraction-free focus.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF64D2FF),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            ),
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ThemeOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = "Selected",
                tint = Color(0xFF64D2FF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF64D2FF),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF64D2FF),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}
