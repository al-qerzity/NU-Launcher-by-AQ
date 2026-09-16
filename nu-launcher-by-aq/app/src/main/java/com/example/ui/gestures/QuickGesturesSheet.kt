package com.example.ui.gestures

import android.content.Context
import android.media.AudioManager
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashlightOff
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiquidQuickTile
import com.example.util.LauncherUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickGesturesSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    var isFlashlightOn by remember { mutableStateOf(false) }
    var currentRingerMode by remember { mutableIntStateOf(LauncherUtils.getRingerMode(context)) }
    val isDeviceAdminActive = remember { LauncherUtils.isDeviceAdminActive(context) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF10131B).copy(alpha = 0.94f),
        scrimColor = Color.Black.copy(alpha = 0.65f),
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
        modifier = Modifier.testTag("quick_gestures_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Quick Controls",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Gesture actions & common system controls",
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
                        contentDescription = "Close Controls",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid of Quick Actions
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Flashlight Toggle
                item {
                    val active = isFlashlightOn
                    LiquidQuickTile(
                        icon = if (active) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
                        title = "Torch",
                        subtitle = if (active) "Active" else "Off",
                        isActive = active,
                        activeColor = Color(0xFFFFD60A),
                        onClick = {
                            isFlashlightOn = LauncherUtils.toggleFlashlight(context, isFlashlightOn)
                        }
                    )
                }

                // 2. Sound Mode (Ring, Vibrate, Silent)
                item {
                    val (icon, subtitle, color) = when (currentRingerMode) {
                        AudioManager.RINGER_MODE_SILENT -> Triple(
                            Icons.Rounded.NotificationsOff,
                            "Silent",
                            Color(0xFFFF453A)
                        )
                        AudioManager.RINGER_MODE_VIBRATE -> Triple(
                            Icons.Rounded.Vibration,
                            "Vibrate",
                            Color(0xFFFF9F0A)
                        )
                        else -> Triple(
                            Icons.Rounded.NotificationsActive,
                            "Ring",
                            Color(0xFF30D158)
                        )
                    }

                    LiquidQuickTile(
                        icon = icon,
                        title = "Sound Mode",
                        subtitle = subtitle,
                        isActive = currentRingerMode != AudioManager.RINGER_MODE_NORMAL,
                        activeColor = color,
                        onClick = {
                            currentRingerMode = LauncherUtils.cycleRingerMode(context)
                        }
                    )
                }

                // 3. Wi-Fi Panel / Settings
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.Wifi,
                        title = "Wi-Fi",
                        subtitle = "Networks",
                        isActive = false,
                        onClick = {
                            LauncherUtils.openWifiSettings(context)
                            onDismiss()
                        }
                    )
                }

                // 4. Bluetooth Panel
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.Bluetooth,
                        title = "Bluetooth",
                        subtitle = "Devices",
                        isActive = false,
                        onClick = {
                            LauncherUtils.openBluetoothSettings(context)
                            onDismiss()
                        }
                    )
                }

                // 5. Display & Brightness
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.BrightnessMedium,
                        title = "Display",
                        subtitle = "Brightness & Night",
                        isActive = false,
                        onClick = {
                            LauncherUtils.openDisplaySettings(context)
                            onDismiss()
                        }
                    )
                }

                // 6. Battery / Power
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.BatteryChargingFull,
                        title = "Battery",
                        subtitle = "Power Saver",
                        isActive = false,
                        onClick = {
                            LauncherUtils.openBatterySettings(context)
                            onDismiss()
                        }
                    )
                }

                // 7. Lock Screen (Available if Device Admin is active)
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.Lock,
                        title = "Lock Screen",
                        subtitle = if (isDeviceAdminActive) "Instant Lock" else "Enable Admin First",
                        isActive = isDeviceAdminActive,
                        activeColor = Color(0xFF64D2FF),
                        onClick = {
                            if (isDeviceAdminActive) {
                                LauncherUtils.lockScreenIfAdmin(context)
                                onDismiss()
                            } else {
                                // Direct to admin activation
                                context.startActivity(LauncherUtils.buildEnableDeviceAdminIntent(context))
                            }
                        }
                    )
                }

                // 8. Set as Default Launcher
                item {
                    LiquidQuickTile(
                        icon = Icons.Rounded.Home,
                        title = "Default Home",
                        subtitle = "Set as default",
                        isActive = true,
                        activeColor = Color(0xFFBF5AF2),
                        onClick = {
                            LauncherUtils.openDefaultAppsSettings(context)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Expand Notifications & System Settings row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expand notifications panel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            LauncherUtils.expandNotificationShade(context)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Notifications Shade",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                // System Settings
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .border(
                            BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            LauncherUtils.openSystemSettings(context)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "All Settings",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
