package com.example.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * DeviceAdminReceiver enables Android's system-level uninstall protection.
 * When active, Android OS blocks uninstallation of NU Launcher from the
 * home screen, app drawer, and system settings app until explicitly deactivated
 * or the device is factory reset.
 */
class LauncherDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "NU Launcher: Uninstall Protection Enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Deactivating protection will allow NU Launcher to be uninstalled."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "NU Launcher: Protection Disabled", Toast.LENGTH_SHORT).show()
    }
}
