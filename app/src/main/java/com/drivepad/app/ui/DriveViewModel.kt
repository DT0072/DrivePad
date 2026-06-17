package com.drivepad.app.ui

import android.app.Application
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import android.os.SystemClock
import androidx.core.content.ContextCompat
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
import com.drivepad.app.media.ExternalMediaQueueItem
import com.drivepad.app.media.ExternalPlaybackSnapshot
import com.drivepad.app.media.MediaNotificationListenerService
import io.ktor.client.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Main ViewModel for the DrivePad application.
 * Manages weather, media state, radio, and preferences.
 */
class DriveViewModel(application: Application) : AndroidViewModel(application) {

    private val httpClient = HttpClient(Android)
    private val weatherApi = WeatherApiClient(httpClient)
    private val radioApi = RadioBrowserApiClient(httpClient)
    private val locationManager =
        application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val preferences = DrivePreferences(application.dataStore)

    private val mediaPackages = mapOf(
        "spotify" to "com.spotify.music",
        "ytmusic" to "com.google.android.apps.youtube.music",
    )
    private val radioPresetJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

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

    private val _nowPlayingAlbumArt = MutableStateFlow<Any?>(null)
    val nowPlayingAlbumArt: StateFlow<Any?> = _nowPlayingAlbumArt.asStateFlow()

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

    private val _activeMediaPackage = MutableStateFlow("")
    val activeMediaPackage: StateFlow<String> = _activeMediaPackage.asStateFlow()

    private val _hasMediaControlAccess = MutableStateFlow(false)
    val hasMediaControlAccess: StateFlow<Boolean> =
        _hasMediaControlAccess.asStateFlow()

    private val _mediaVolume = MutableStateFlow(0f)
    val mediaVolume: StateFlow<Float> = _mediaVolume.asStateFlow()

    private val _mediaQueue = MutableStateFlow<List<ExternalMediaQueueItem>>(emptyList())
    val mediaQueue: StateFlow<List<ExternalMediaQueueItem>> = _mediaQueue.asStateFlow()

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
        loadRadioPresets()
        loadWeather()
        loadRadioStations()
        externalMediaController.refresh()
    }

    // -- Weather --
    private fun loadWeather() {
        viewModelScope.launch {
            while (true) {
                refreshWeather()
                delay(WEATHER_REFRESH_INTERVAL_MS)
            }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            val savedLat = preferences.weatherLatitude.first()
            val savedLon = preferences.weatherLongitude.first()
            val location = getCurrentLocation()
            val lat = location?.latitude ?: savedLat
            val lon = location?.longitude ?: savedLon
            if (location != null) {
                preferences.setWeatherLocation(lat, lon)
            }
            val data = weatherApi.getCurrentWeather(lat, lon)
            _weather.value = data
        }
    }

    private suspend fun getCurrentLocation(): Location? {
        val application = getApplication<Application>()
        val permissionGranted = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) return null

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            else -> return null
        }

        return try {
            suspendCancellableCoroutine { continuation ->
                val cancellationSignal = CancellationSignal()
                continuation.invokeOnCancellation { cancellationSignal.cancel() }
                locationManager.getCurrentLocation(
                    provider,
                    cancellationSignal,
                    ContextCompat.getMainExecutor(application),
                ) { location ->
                    if (continuation.isActive) continuation.resume(location)
                }
            }
        } catch (_: SecurityException) {
            null
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

    fun updateNowPlaying(title: String, artist: String, album: String, albumArt: Bitmap?) {
        _nowPlayingTitle.value = title
        _nowPlayingArtist.value = artist
        _nowPlayingAlbum.value = album
        _nowPlayingAlbumArt.value = albumArt
    }

    // -- Radio --
    private fun loadRadioPresets() {
        viewModelScope.launch {
            preferences.radioPresetsJson.collect { saved ->
                if (saved.isBlank()) return@collect
                val decoded = runCatching {
                    radioPresetJson.decodeFromString<List<RadioStation?>>(saved)
                }.getOrNull() ?: return@collect
                _radioPresets.value = List(6) { index -> decoded.getOrNull(index) }
            }
        }
    }

    private fun loadRadioStations() {
        viewModelScope.launch {
            val countryCode = preferences.radioCountryCode.first()
            val stations = radioApi.getStationsByCountry(countryCode, 50)
            _radioStations.value = stations
            if (stations.isNotEmpty() && _currentStation.value == null) {
                _currentStation.value = stations.first()
                _currentFrequency.value = radioFrequencyFor(stations.first(), stations, 0)
            }
        }
    }

    fun selectRadioStation(station: RadioStation) {
        _currentStation.value = station
        _currentFrequency.value = radioFrequencyFor(station)
        playRadioStation(station)
    }

    fun setRadioFrequency(freq: Float) {
        val tuned = freq.coerceIn(88f, 108f).roundToTenth()
        _currentFrequency.value = tuned

        val stations = _radioStations.value
        val nearest = stations.withIndex().minByOrNull { indexed ->
            abs(radioFrequencyFor(indexed.value, stations, indexed.index) - tuned)
        } ?: return

        val nearestFrequency = radioFrequencyFor(nearest.value, stations, nearest.index)
        if (abs(nearestFrequency - tuned) <= 0.35f) {
            val stationChanged =
                nearest.value.stationUuid != _currentStation.value?.stationUuid
            _currentStation.value = nearest.value
            if (stationChanged && radioPlayer.playWhenReady) {
                playRadioStation(nearest.value)
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
        if (slotIndex !in 0 until 6) return
        val current = _currentStation.value ?: return
        val presets = _radioPresets.value.toMutableList()
        presets[slotIndex] = current
        _radioPresets.value = presets
        persistRadioPresets(presets)
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

    private fun persistRadioPresets(presets: List<RadioStation?>) {
        viewModelScope.launch {
            preferences.setRadioPresetsJson(
                radioPresetJson.encodeToString(List(6) { index -> presets.getOrNull(index) }),
            )
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

    private fun radioFrequencyFor(
        station: RadioStation,
        stations: List<RadioStation> = _radioStations.value,
        knownIndex: Int = stations.indexOfFirst { it.stationUuid == station.stationUuid },
    ): Float {
        val published = station.getDisplayFrequency().toFloatOrNull()
        if (published != null && published in 88f..108f) {
            return published.roundToTenth()
        }

        val index = knownIndex.coerceAtLeast(0)
        return (88f + ((index * 7) % 201) / 10f)
            .coerceIn(88f, 108f)
            .roundToTenth()
    }

    private fun Float.roundToTenth(): Float = (this * 10f).roundToInt() / 10f

    private fun updateExternalMediaState(snapshot: ExternalPlaybackSnapshot) {
        _activeMediaPackage.value = snapshot.packageName
        _nowPlayingTitle.value = snapshot.title
        _nowPlayingArtist.value = snapshot.artist
        _nowPlayingAlbum.value = snapshot.album
        _nowPlayingAlbumArt.value = snapshot.albumArt ?: snapshot.albumArtUri.ifBlank { null }
        _isPlaying.value = snapshot.isPlaying
        _mediaVolume.value = snapshot.volume
        _mediaQueue.value = snapshot.queue

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

    fun skipToQueueItem(queueId: Long) {
        externalMediaController.skipToQueueItem(queueId)
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

    private companion object {
        const val WEATHER_REFRESH_INTERVAL_MS = 10 * 60 * 1_000L
    }
}
