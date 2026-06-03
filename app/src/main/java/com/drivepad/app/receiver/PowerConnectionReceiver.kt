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
 * Launches DrivePad when vehicle power (USB/charger) is connected.
 * Only activates if the user has enabled auto-launch in settings.
 */
class PowerConnectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                val prefs = DrivePreferences(context.dataStore)
                val autoLaunch = runBlocking { prefs.autoLaunchOnPower.first() }
                if (autoLaunch) {
                    val launchIntent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    }
                    context.startActivity(launchIntent)
                }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                val prefs = DrivePreferences(context.dataStore)
                val autoExit = runBlocking { prefs.autoExitOnDisconnect.first() }
                if (autoExit) {
                    // Send minimize intent — the Activity will handle this
                    val minimizeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(minimizeIntent)
                }
            }
        }
    }
}
