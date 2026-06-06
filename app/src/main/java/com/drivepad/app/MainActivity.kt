package com.drivepad.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepad.app.data.preferences.ThemeMode
import com.drivepad.app.ui.DriveApp
import com.drivepad.app.ui.DriveViewModel
import com.drivepad.app.ui.theme.DriveTheme

/**
 * Main entry point for DrivePad infotainment system.
 * Supports both normal app mode and optional launcher mode.
 * Landscape-locked for automotive use.
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DriveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[DriveViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()

            val useDarkTheme = when (themeMode) {
                ThemeMode.AUTO -> systemDark
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            DriveTheme(darkTheme = useDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DriveApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized) {
            viewModel.refreshMediaSession()
        }
    }
}
