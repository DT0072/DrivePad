package com.drivepad.app.ui

import android.app.Application
import android.content.ComponentName
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.drivepad.app.data.api.RadioBrowserApiClient
import com.drivepad.app.data.api.RadioStation
import com.drivepad.app.data.api.WeatherApiClient
import com.drivepad.app.data.api.WeatherData
import com.drivepad.app.data.preferences.DrivePreferences
import com.drivepad.app.data.preferences.ThemeMode
import com.drivepad.app.data.preferences.dataStore
import com.drivepad.app.media.ExternalMediaSessionController
import com.drivepad.app.media.ExternalPlaybackSnapshot
import com.drivepad.app.media.MediaNotificationListenerService
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val mediaPackages = mapOf(
        "spotify" to "com.spotify.music",
        "ytmusic" to "com.google.android.apps.youtube.music",
        "huawei" to "com.huawei.music",
    )

    private var mediaProgressJob: Job? = null
    private var mediaPositionMs = 0L
    private var mediaDurationMs = 0L
    private var mediaPlaybackSpeed = 1f
    private var mediaPositionUpdatedAt = SystemClock.elapsedRealtime()

    private val externalMediaController = ExternalMediaSessionController(
        context = application,
        listenerComponent = ComponentName(
            application,
            MediaNotificationListenerService::class.java,
        ),
        onSnapshotChanged = ::updateExternalMediaState,
        onAccessChanged = { _hasMediaControlAccess.value = it },
    )

    private val radioPlayer = ExoPlayer.Builder(application).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true,
        )
        addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                _isRadioPlaying.value = playWhenReady
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    _isRadioPlaying.value = false
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _isRadioPlaying.value = false
                _radioPlaybackError.value =
                    error.localizedMessage ?: "Unable to play this station."
            }
        })
    }

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

    private val _hasMediaControlAccess = MutableStateFlow(false)
    val hasMediaControlAccess: StateFlow<Boolean> =
        _hasMediaControlAccess.asStateFlow()

    private val _mediaVolume = MutableStateFlow(0f)
    val mediaVolume: StateFlow<Float> = _mediaVolume.asStateFlow()

    // -- Radio State --
    private val _radioStations = MutableStateFlow<List<RadioStation>>(emptyList())
    val radioStations: StateFlow<List<RadioStation>> = _radioStations.asStateFlow()

    private val _currentStation = MutableStateFlow<RadioStation?>(null)
    val currentStation: StateFlow<RadioStation?> = _currentStation.asStateFlow()

    private val _currentFrequency = MutableStateFlow(98.8f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()

    private val _isRadioPlaying = MutableStateFlow(false)
    val isRadioPlaying: StateFlow<Boolean> = _isRadioPlaying.asStateFlow()

    private val _radioPlaybackError = MutableStateFlow<String?>(null)
    val radioPlaybackError: StateFlow<String?> = _radioPlaybackError.asStateFlow()

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
        externalMediaController.refresh()
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
        externalMediaController.togglePlayPause()
    }

    fun skipNext() {
        externalMediaController.skipNext()
    }

    fun skipPrevious() {
        externalMediaController.skipPrevious()
    }

    fun seekTo(progress: Float) {
        externalMediaController.seekTo(progress)
    }

    fun setActiveMediaSource(sourceId: String) {
        _activeMediaSource.value = sourceId
        externalMediaController.setPreferredPackage(mediaPackages[sourceId])
    }

    fun setMediaVolume(volume: Float) {
        _mediaVolume.value = volume.coerceIn(0f, 1f)
        externalMediaController.setVolume(_mediaVolume.value)
    }

    fun refreshMediaSession() {
        externalMediaController.refresh()
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
        playRadioStation(station)
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
                val stationChanged =
                    nearest.stationUuid != _currentStation.value?.stationUuid
                _currentStation.value = nearest
                if (stationChanged && radioPlayer.playWhenReady) {
                    playRadioStation(nearest)
                }
            }
        }
    }

    fun toggleRadioPlayPause() {
        if (radioPlayer.playWhenReady) {
            radioPlayer.pause()
        } else {
            val station = _currentStation.value ?: _radioStations.value.firstOrNull()
            if (station != null) {
                _currentStation.value = station
                playRadioStation(station)
            }
        }
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

    private fun playRadioStation(station: RadioStation) {
        val streamUrl = station.streamUrl
        if (streamUrl.isBlank()) {
            _radioPlaybackError.value = "This station does not provide a playable stream."
            _isRadioPlaying.value = false
            return
        }

        _radioPlaybackError.value = null
        val currentUrl = radioPlayer.currentMediaItem
            ?.localConfiguration
            ?.uri
            ?.toString()
        if (currentUrl != streamUrl) {
            radioPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
            radioPlayer.prepare()
        }
        radioPlayer.play()
        _isRadioPlaying.value = true
    }

    private fun updateExternalMediaState(snapshot: ExternalPlaybackSnapshot) {
        _nowPlayingTitle.value = snapshot.title
        _nowPlayingArtist.value = snapshot.artist
        _nowPlayingAlbum.value = snapshot.album
        _isPlaying.value = snapshot.isPlaying
        _mediaVolume.value = snapshot.volume

        mediaPositionMs = snapshot.positionMs
        mediaDurationMs = snapshot.durationMs
        mediaPlaybackSpeed = snapshot.playbackSpeed
        mediaPositionUpdatedAt = SystemClock.elapsedRealtime()
        publishMediaProgress(mediaPositionMs)

        mediaPackages.entries
            .firstOrNull { it.value == snapshot.packageName }
            ?.let { _activeMediaSource.value = it.key }

        mediaProgressJob?.cancel()
        if (snapshot.isPlaying && snapshot.durationMs > 0L) {
            mediaProgressJob = viewModelScope.launch {
                while (true) {
                    delay(1_000)
                    val elapsedMs = SystemClock.elapsedRealtime() - mediaPositionUpdatedAt
                    val currentPosition = mediaPositionMs +
                        (elapsedMs * mediaPlaybackSpeed).toLong()
                    publishMediaProgress(currentPosition)
                }
            }
        }
    }

    private fun publishMediaProgress(positionMs: Long) {
        val boundedPosition = if (mediaDurationMs > 0L) {
            positionMs.coerceIn(0L, mediaDurationMs)
        } else {
            positionMs.coerceAtLeast(0L)
        }
        _playbackProgress.value = if (mediaDurationMs > 0L) {
            boundedPosition.toFloat() / mediaDurationMs
        } else {
            0f
        }
        _currentPosition.value = formatDuration(boundedPosition)
        _totalDuration.value = formatDuration(mediaDurationMs)
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs.coerceAtLeast(0L) / 1_000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
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
        mediaProgressJob?.cancel()
        externalMediaController.release()
        radioPlayer.release()
        httpClient.close()
    }
}
