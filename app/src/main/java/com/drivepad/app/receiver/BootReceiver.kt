package com.drivepad.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.drivepad.app.MainActivity
import com.drivepad.app.data.preferences.DrivePreferences
import com.drivepad.app.data.preferences.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Launches DrivePad when the device boots up.
 * Only activates if the user has enabled auto-launch on boot in settings.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = DrivePreferences(context.dataStore)
            val autoLaunch = runBlocking { prefs.autoLaunchOnBoot.first() }
            if (autoLaunch) {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(launchIntent)
            }
        }
    }
}
