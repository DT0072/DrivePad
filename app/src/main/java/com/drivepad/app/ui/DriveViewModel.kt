package com.drivepad.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.drivepad.app.data.api.RadioBrowserApiClient
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.data.api.WeatherApiClient
import com.drivepad.app.data.api.WeatherData
import com.drivepad.app.data.preferences.DrivePreferences
import com.drivepad.app.data.preferences.ThemeMode
import com.drivepad.app.data.preferences.dataStore
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Main ViewModel for the DrivePad application.
 * Manages weather, media state, radio, and preferences.
 */
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val httpClient = HttpClient(Android)
    private val weatherApi = WeatherApiClient(httpClient)
    private val radioApi = RadioBrowserApiClient(httpClient)
    val preferences = DrivePreferences(application.dataStore)

    // -- Weather State --
    private val _weather = MutableStateFlow<WeatherData?>(null)
    val weather: StateFlow<WeatherData?> = _weather.asStateFlow()

    // -- Media State --
    private val _nowPlayingTitle = MutableStateFlow("")
    val nowPlayingTitle: StateFlow<String> = _nowPlayingTitle.asStateFlow()

    private val _nowPlayingArtist = MutableStateFlow("")
    val nowPlayingArtist: StateFlow<String> = _nowPlayingArtist.asStateFlow()

    private val _nowPlayingAlbum = MutableStateFlow("")
    val nowPlayingAlbum: StateFlow<String> = _nowPlayingAlbum.asStateFlow()

    private val _nowPlayingAlbumArt = MutableStateFlow("")
    val nowPlayingAlbumArt: StateFlow<String> = _nowPlayingAlbumArt.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _currentPosition = MutableStateFlow("0:00")
    val currentPosition: StateFlow<String> = _currentPosition.asStateFlow()

    private val _totalDuration = MutableStateFlow("0:00")
    val totalDuration: StateFlow<String> = _totalDuration.asStateFlow()

    private val _activeMediaSource = MutableStateFlow("spotify")
    val activeMediaSource: StateFlow<String> = _activeMediaSource.asStateFlow()

    // -- Radio State --
    private val _radioStations = MutableStateFlow<List<RadioStation>>(emptyList())
    val radioStations: StateFlow<List<RadioStation>> = _radioStations.asStateFlow()

    private val _currentStation = MutableStateFlow<RadioStation?>(null)
    val currentStation: StateFlow<RadioStation?> = _currentStation.asStateFlow()

    private val _currentFrequency = MutableStateFlow(98.8f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()

    private val _isRadioPlaying = MutableStateFlow(false)
    val isRadioPlaying: StateFlow<Boolean> = _isRadioPlaying.asStateFlow()

    private val _radioPresets = MutableStateFlow<List<RadioStation?>>(List(6) { null })
    val radioPresets: StateFlow<List<RadioStation?>> = _radioPresets.asStateFlow()

    // -- Settings State --
    val themeMode: StateFlow<ThemeMode> = preferences.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.AUTO)

    val launcherModeEnabled: StateFlow<Boolean> = preferences.launcherModeEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val autoLaunchOnPower: StateFlow<Boolean> = preferences.autoLaunchOnPower
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val autoExitOnDisconnect: StateFlow<Boolean> = preferences.autoExitOnDisconnect
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val autoLaunchOnBoot: StateFlow<Boolean> = preferences.autoLaunchOnBoot
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        loadWeather()
        loadRadioStations()
    }

    // -- Weather --
    private fun loadWeather() {
        viewModelScope.launch {
            preferences.weatherLatitude.combine(preferences.weatherLongitude) { lat, lon ->
                Pair(lat, lon)
            }.collect { (lat, lon) ->
                val data = weatherApi.getCurrentWeather(lat, lon)
                _weather.value = data
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val lat = preferences.weatherLatitude.first()
            val lon = preferences.weatherLongitude.first()
            val data = weatherApi.getCurrentWeather(lat, lon)
            _weather.value = data
        }
    }

    // -- Media Controls --
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun skipNext() {
        // In production, this would send to active MediaController
    }

    fun skipPrevious() {
        // In production, this would send to active MediaController
    }

    fun seekTo(progress: Float) {
        _playbackProgress.value = progress
    }

    fun setActiveMediaSource(sourceId: String) {
        _activeMediaSource.value = sourceId
    }

    fun updateNowPlaying(title: String, artist: String, album: String, albumArt: String) {
        _nowPlayingTitle.value = title
        _nowPlayingArtist.value = artist
        _nowPlayingAlbum.value = album
        _nowPlayingAlbumArt.value = albumArt
    }

    // -- Radio --
    private fun loadRadioStations() {
        viewModelScope.launch {
            val countryCode = preferences.radioCountryCode.first()
            val stations = radioApi.getStationsByCountry(countryCode, 50)
            _radioStations.value = stations
            if (stations.isNotEmpty() && _currentStation.value == null) {
                _currentStation.value = stations.first()
                val freq = stations.first().getDisplayFrequency()
                if (freq.isNotEmpty()) {
                    _currentFrequency.value = freq.toFloatOrNull() ?: 98.8f
                }
            }
        }
    }

    fun selectRadioStation(station: RadioStation) {
        _currentStation.value = station
        val freq = station.getDisplayFrequency()
        if (freq.isNotEmpty()) {
            _currentFrequency.value = freq.toFloatOrNull() ?: _currentFrequency.value
        }
        _isRadioPlaying.value = true
    }

    fun setRadioFrequency(freq: Float) {
        _currentFrequency.value = freq.coerceIn(88f, 108f)
        // Find nearest station
        val nearest = _radioStations.value.minByOrNull { station ->
            val stationFreq = station.getDisplayFrequency().toFloatOrNull() ?: Float.MAX_VALUE
            kotlin.math.abs(stationFreq - freq)
        }
        if (nearest != null) {
            val nearestFreq = nearest.getDisplayFrequency().toFloatOrNull()
            if (nearestFreq != null && kotlin.math.abs(nearestFreq - freq) < 0.5f) {
                _currentStation.value = nearest
            }
        }
    }

    fun toggleRadioPlayPause() {
        _isRadioPlaying.value = !_isRadioPlaying.value
    }

    fun seekRadioForward() {
        val stations = _radioStations.value
        val current = _currentStation.value
        if (stations.isNotEmpty()) {
            val idx = stations.indexOf(current)
            val next = stations.getOrElse(idx + 1) { stations.first() }
            selectRadioStation(next)
        }
    }

    fun seekRadioBackward() {
        val stations = _radioStations.value
        val current = _currentStation.value
        if (stations.isNotEmpty()) {
            val idx = stations.indexOf(current)
            val prev = stations.getOrElse(idx - 1) { stations.last() }
            selectRadioStation(prev)
        }
    }

    fun savePreset(slotIndex: Int) {
        val current = _currentStation.value ?: return
        val presets = _radioPresets.value.toMutableList()
        presets[slotIndex] = current
        _radioPresets.value = presets
    }

    fun loadPreset(slotIndex: Int) {
        val preset = _radioPresets.value.getOrNull(slotIndex) ?: return
        selectRadioStation(preset)
    }

    fun autoScanStations() {
        loadRadioStations()
    }

    fun searchRadioStations(query: String) {
        viewModelScope.launch {
            val results = radioApi.searchStations(query, 30)
            _radioStations.value = results
        }
    }

    // -- Settings --
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    fun setLauncherMode(enabled: Boolean) {
        viewModelScope.launch { preferences.setLauncherMode(enabled) }
    }

    fun setAutoLaunchOnPower(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoLaunchOnPower(enabled) }
    }

    fun setAutoExitOnDisconnect(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoExitOnDisconnect(enabled) }
    }

    fun setAutoLaunchOnBoot(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoLaunchOnBoot(enabled) }
    }

    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}
