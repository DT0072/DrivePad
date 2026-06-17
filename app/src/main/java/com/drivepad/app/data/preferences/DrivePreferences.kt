package com.drivepad.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drivepad_settings")

class DrivePreferences(private val dataStore: DataStore<Preferences>) {

    // Theme
    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.entries.getOrElse(prefs[THEME_MODE] ?: 0) { ThemeMode.AUTO }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs -> prefs[THEME_MODE] = mode.ordinal }
    }

    // Launcher mode
    val launcherModeEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[LAUNCHER_MODE] ?: false
    }

    suspend fun setLauncherMode(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[LAUNCHER_MODE] = enabled }
    }

    // Auto-launch on power
    val autoLaunchOnPower: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_LAUNCH_POWER] ?: false
    }

    suspend fun setAutoLaunchOnPower(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[AUTO_LAUNCH_POWER] = enabled }
    }

    // Auto-exit on disconnect
    val autoExitOnDisconnect: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_EXIT_DISCONNECT] ?: false
    }

    suspend fun setAutoExitOnDisconnect(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[AUTO_EXIT_DISCONNECT] = enabled }
    }

    // Auto-launch on boot
    val autoLaunchOnBoot: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_LAUNCH_BOOT] ?: false
    }

    suspend fun setAutoLaunchOnBoot(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[AUTO_LAUNCH_BOOT] = enabled }
    }

    // Weather location
    val weatherLatitude: Flow<Double> = dataStore.data.map { prefs ->
        prefs[WEATHER_LAT] ?: 3.1390  // Default: Kuala Lumpur
    }

    val weatherLongitude: Flow<Double> = dataStore.data.map { prefs ->
        prefs[WEATHER_LON] ?: 101.6869  // Default: Kuala Lumpur
    }

    suspend fun setWeatherLocation(lat: Double, lon: Double) {
        dataStore.edit { prefs ->
            prefs[WEATHER_LAT] = lat
            prefs[WEATHER_LON] = lon
        }
    }

    // Radio country code
    val radioCountryCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[RADIO_COUNTRY] ?: "MY"  // Default: Malaysia
    }

    suspend fun setRadioCountryCode(code: String) {
        dataStore.edit { prefs -> prefs[RADIO_COUNTRY] = code }
    }

    val radioPresetsJson: Flow<String> = dataStore.data.map { prefs ->
        prefs[RADIO_PRESETS_JSON] ?: ""
    }

    suspend fun setRadioPresetsJson(json: String) {
        dataStore.edit { prefs -> prefs[RADIO_PRESETS_JSON] = json }
    }

    // Last active screen
    val lastActiveScreen: Flow<String> = dataStore.data.map { prefs ->
        prefs[LAST_SCREEN] ?: "home"
    }

    suspend fun setLastActiveScreen(screen: String) {
        dataStore.edit { prefs -> prefs[LAST_SCREEN] = screen }
    }

    companion object {
        private val THEME_MODE = intPreferencesKey("theme_mode")
        private val LAUNCHER_MODE = booleanPreferencesKey("launcher_mode")
        private val AUTO_LAUNCH_POWER = booleanPreferencesKey("auto_launch_power")
        private val AUTO_EXIT_DISCONNECT = booleanPreferencesKey("auto_exit_disconnect")
        private val AUTO_LAUNCH_BOOT = booleanPreferencesKey("auto_launch_boot")
        private val WEATHER_LAT = doublePreferencesKey("weather_lat")
        private val WEATHER_LON = doublePreferencesKey("weather_lon")
        private val RADIO_COUNTRY = stringPreferencesKey("radio_country")
        private val RADIO_PRESETS_JSON = stringPreferencesKey("radio_presets_json")
        private val LAST_SCREEN = stringPreferencesKey("last_screen")
    }
}

enum class ThemeMode {
    AUTO,   // Follow system
    DARK,   // Always dark (night driving)
    LIGHT   // Always light (day driving)
}
