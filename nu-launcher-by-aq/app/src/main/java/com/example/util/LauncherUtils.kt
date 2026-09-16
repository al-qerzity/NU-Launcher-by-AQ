package com.example.util

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.provider.Telephony
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Forum
import com.example.model.EssentialAppInfo
import com.example.model.EssentialAppType
import com.example.receiver.LauncherDeviceAdminReceiver
import java.lang.reflect.Method

object LauncherUtils {

    const val WHATSAPP_PACKAGE = "com.whatsapp"
    const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"

    fun performHaptic(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator?.vibrate(20)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibration not available
        }
    }

    // --- Essential Apps Detection & Launching ---

    fun getEssentialApps(context: Context): List<EssentialAppInfo> {
        val pm = context.packageManager

        // 1. Phone App
        val phoneIntent = Intent(Intent.ACTION_DIAL)
        val phoneResolve = pm.resolveActivity(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val phonePkg = phoneResolve?.activityInfo?.packageName
        val phoneApp = EssentialAppInfo(
            type = EssentialAppType.PHONE,
            title = "Phone",
            subtitle = if (phonePkg != null) "Dialer & Recents" else "System Dialer",
            packageName = phonePkg,
            isAvailable = true,
            iconVector = Icons.Rounded.Call,
            accentColorHex = 0xFF4ADE80 // Emerald green
        )

        // 2. Messaging App
        val defaultSmsPkg = Telephony.Sms.getDefaultSmsPackage(context)
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
        val smsResolve = pm.resolveActivity(smsIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val messagingPkg = defaultSmsPkg ?: smsResolve?.activityInfo?.packageName
        val messagingApp = EssentialAppInfo(
            type = EssentialAppType.MESSAGING,
            title = "Messages",
            subtitle = if (messagingPkg != null) "SMS & Chat" else "System Messaging",
            packageName = messagingPkg,
            isAvailable = true,
            iconVector = Icons.Rounded.ChatBubble,
            accentColorHex = 0xFF60A5FA // Electric blue
        )

        // 3. WhatsApp (or WhatsApp Business)
        val whatsappPkg = when {
            isPackageInstalled(pm, WHATSAPP_PACKAGE) -> WHATSAPP_PACKAGE
            isPackageInstalled(pm, WHATSAPP_BUSINESS_PACKAGE) -> WHATSAPP_BUSINESS_PACKAGE
            else -> null
        }
        val whatsappApp = EssentialAppInfo(
            type = EssentialAppType.WHATSAPP,
            title = if (whatsappPkg == WHATSAPP_BUSINESS_PACKAGE) "WhatsApp Business" else "WhatsApp",
            subtitle = if (whatsappPkg != null) "Connected" else "Not installed",
            packageName = whatsappPkg,
            isAvailable = whatsappPkg != null,
            iconVector = Icons.Rounded.Forum,
            accentColorHex = 0xFF25D366 // WhatsApp green
        )

        return listOf(phoneApp, messagingApp, whatsappApp)
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean {
        return try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun launchEssentialApp(context: Context, app: EssentialAppInfo) {
        performHaptic(context)
        val pm = context.packageManager
        when (app.type) {
            EssentialAppType.PHONE -> {
                val dialIntent = if (app.packageName != null) {
                    pm.getLaunchIntentForPackage(app.packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    } ?: Intent(Intent.ACTION_DIAL).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                } else {
                    Intent(Intent.ACTION_DIAL).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                }
                try {
                    context.startActivity(dialIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open Phone app", Toast.LENGTH_SHORT).show()
                }
            }

            EssentialAppType.MESSAGING -> {
                val smsIntent = if (app.packageName != null) {
                    pm.getLaunchIntentForPackage(app.packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    } ?: Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                } else {
                    Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
                try {
                    context.startActivity(smsIntent)
                } catch (e: Exception) {
                    try {
                        val fallback = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(fallback)
                    } catch (_: Exception) {
                        Toast.makeText(context, "Could not open Messaging app", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            EssentialAppType.WHATSAPP -> {
                if (app.isAvailable && app.packageName != null) {
                    val waIntent = pm.getLaunchIntentForPackage(app.packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    if (waIntent != null) {
                        try {
                            context.startActivity(waIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to launch WhatsApp", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Not installed, provide intent or prompt
                    Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- Device Administrator / Uninstall Protection ---

    fun getDeviceAdminComponent(context: Context): ComponentName {
        return ComponentName(context, LauncherDeviceAdminReceiver::class.java)
    }

    fun isDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        return dpm?.isAdminActive(getDeviceAdminComponent(context)) == true
    }

    fun buildEnableDeviceAdminIntent(context: Context): Intent {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getDeviceAdminComponent(context))
        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Protects NU Launcher against uninstallation. While this administrator is active, Android blocks uninstallation through settings and home screens. It can only be uninstalled after deactivating administrator or factory resetting."
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    fun removeDeviceAdmin(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        return try {
            dpm?.removeActiveAdmin(getDeviceAdminComponent(context))
            true
        } catch (_: Exception) {
            false
        }
    }

    fun lockScreenIfAdmin(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        return if (dpm != null && isDeviceAdminActive(context)) {
            try {
                dpm.lockNow()
                true
            } catch (_: Exception) {
                false
            }
        } else {
            false
        }
    }

    // --- System Settings & Gestures Shortcuts ---

    fun toggleFlashlight(context: Context, currentState: Boolean): Boolean {
        performHaptic(context)
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            ?: return false
        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            val newState = !currentState
            cameraManager.setTorchMode(cameraId, newState)
            newState
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
            currentState
        }
    }

    fun cycleRingerMode(context: Context): Int {
        performHaptic(context)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return AudioManager.RINGER_MODE_NORMAL
        val nextMode = when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
            AudioManager.RINGER_MODE_VIBRATE -> AudioManager.RINGER_MODE_SILENT
            else -> AudioManager.RINGER_MODE_NORMAL
        }
        try {
            audioManager.ringerMode = nextMode
        } catch (_: Exception) {
            // Some newer Android versions require DND policy access for RINGER_MODE_SILENT
            try {
                audioManager.ringerMode = if (nextMode == AudioManager.RINGER_MODE_SILENT) {
                    AudioManager.RINGER_MODE_VIBRATE
                } else {
                    AudioManager.RINGER_MODE_NORMAL
                }
            } catch (_: Exception) {}
        }
        return audioManager.ringerMode
    }

    fun getRingerMode(context: Context): Int {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        return audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
    }

    fun openWifiSettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStartActivity(context, intent)
    }

    fun openBluetoothSettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStartActivity(context, intent)
    }

    fun openDisplaySettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStartActivity(context, intent)
    }

    fun openBatterySettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (!safeStartActivity(context, intent)) {
            safeStartActivity(context, Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun openDefaultAppsSettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (!safeStartActivity(context, intent)) {
            safeStartActivity(context, Intent(Settings.ACTION_HOME_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun openSystemSettings(context: Context) {
        performHaptic(context)
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        safeStartActivity(context, intent)
    }

    fun expandNotificationShade(context: Context) {
        performHaptic(context)
        try {
            @Suppress("WrongConstant")
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand: Method = statusBarManager.getMethod("expandNotificationsPanel")
            expand.invoke(statusBarService)
        } catch (_: Exception) {
            try {
                @Suppress("WrongConstant")
                val statusBarService = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val expand: Method = statusBarManager.getMethod("expand")
                expand.invoke(statusBarService)
            } catch (_: Exception) {
                // Fallback: notify user or open settings
            }
        }
    }

    private fun safeStartActivity(context: Context, intent: Intent): Boolean {
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
