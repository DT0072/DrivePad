package com.drivepad.app.media

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.VolumeProvider
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.core.app.NotificationManagerCompat
import kotlin.math.roundToInt

data class ExternalPlaybackSnapshot(
    val packageName: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumArt: Bitmap? = null,
    val albumArtUri: String = "",
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1f,
    val volume: Float = 0f,
)

class ExternalMediaSessionController(
    private val context: Context,
    private val listenerComponent: ComponentName,
    private val onSnapshotChanged: (ExternalPlaybackSnapshot) -> Unit,
    private val onAccessChanged: (Boolean) -> Unit,
) {
    private val sessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var preferredPackage: String? = null
    private var activeController: MediaController? = null
    private var listenerRegistered = false

    private val activeSessionsListener =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            selectController(controllers.orEmpty())
        }

    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            publishSnapshot()
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            publishSnapshot()
        }

        override fun onAudioInfoChanged(info: MediaController.PlaybackInfo) {
            publishSnapshot()
        }

        override fun onSessionDestroyed() {
            refresh()
        }
    }

    fun refresh() {
        val accessGranted = NotificationManagerCompat
            .getEnabledListenerPackages(context)
            .contains(context.packageName)
        onAccessChanged(accessGranted)

        if (!accessGranted) {
            if (listenerRegistered) {
                sessionManager.removeOnActiveSessionsChangedListener(activeSessionsListener)
                listenerRegistered = false
            }
            setActiveController(null)
            return
        }

        try {
            if (!listenerRegistered) {
                sessionManager.addOnActiveSessionsChangedListener(
                    activeSessionsListener,
                    listenerComponent,
                )
                listenerRegistered = true
            }
            selectController(sessionManager.getActiveSessions(listenerComponent))
        } catch (_: SecurityException) {
            onAccessChanged(false)
            setActiveController(null)
        }
    }

    fun setPreferredPackage(packageName: String?) {
        preferredPackage = packageName
        refresh()
    }

    fun togglePlayPause() {
        val controller = activeController ?: return
        if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
            controller.transportControls.pause()
        } else {
            controller.transportControls.play()
        }
    }

    fun skipNext() {
        activeController?.transportControls?.skipToNext()
    }

    fun skipPrevious() {
        activeController?.transportControls?.skipToPrevious()
    }

    fun seekTo(progress: Float) {
        val controller = activeController ?: return
        val duration = controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        if (duration > 0L) {
            controller.transportControls.seekTo(
                (duration * progress.coerceIn(0f, 1f)).toLong(),
            )
        }
    }

    fun setVolume(progress: Float) {
        val normalized = progress.coerceIn(0f, 1f)
        val playbackInfo = activeController?.playbackInfo
        if (playbackInfo != null &&
            playbackInfo.volumeControl != VolumeProvider.VOLUME_CONTROL_FIXED &&
            playbackInfo.maxVolume > 0
        ) {
            activeController?.setVolumeTo(
                (normalized * playbackInfo.maxVolume).roundToInt(),
                0,
            )
        } else {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (normalized * maxVolume).roundToInt(),
                0,
            )
            publishSnapshot()
        }
    }

    fun release() {
        setActiveController(null)
        if (listenerRegistered) {
            sessionManager.removeOnActiveSessionsChangedListener(activeSessionsListener)
            listenerRegistered = false
        }
    }

    private fun selectController(controllers: List<MediaController>) {
        val selected = controllers.firstOrNull { it.packageName == preferredPackage }
            ?: controllers.firstOrNull {
                it.playbackState?.state == PlaybackState.STATE_PLAYING
            }
            ?: activeController?.let { current ->
                controllers.firstOrNull { it.sessionToken == current.sessionToken }
            }
            ?: controllers.firstOrNull()

        setActiveController(selected)
    }

    private fun setActiveController(controller: MediaController?) {
        if (activeController?.sessionToken == controller?.sessionToken) {
            publishSnapshot()
            return
        }

        activeController?.unregisterCallback(controllerCallback)
        activeController = controller
        activeController?.registerCallback(controllerCallback)
        publishSnapshot()
    }

    private fun publishSnapshot() {
        val controller = activeController
        if (controller == null) {
            onSnapshotChanged(
                ExternalPlaybackSnapshot(volume = getMusicStreamVolume()),
            )
            return
        }

        val metadata = controller.metadata
        val state = controller.playbackState
        val playbackInfo = controller.playbackInfo
        val volume = if (playbackInfo.maxVolume > 0) {
            playbackInfo.currentVolume.toFloat() / playbackInfo.maxVolume
        } else {
            getMusicStreamVolume()
        }

        onSnapshotChanged(
            ExternalPlaybackSnapshot(
                packageName = controller.packageName,
                title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE).orEmpty(),
                artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                    ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST).orEmpty(),
                album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM).orEmpty(),
                albumArt = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                    ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART),
                albumArtUri = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
                    ?: metadata?.getString(MediaMetadata.METADATA_KEY_ART_URI).orEmpty(),
                isPlaying = state?.state == PlaybackState.STATE_PLAYING,
                positionMs = state?.position?.coerceAtLeast(0L) ?: 0L,
                durationMs = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
                    ?.coerceAtLeast(0L) ?: 0L,
                playbackSpeed = state?.playbackSpeed ?: 1f,
                volume = volume.coerceIn(0f, 1f),
            ),
        )
    }

    private fun getMusicStreamVolume(): Float {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return if (maxVolume > 0) {
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxVolume
        } else {
            0f
        }
    }
}
