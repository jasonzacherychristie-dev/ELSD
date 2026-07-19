package com.elsd.bus

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

/**
 * PLATE bus — local file or stream (HLS) via Media3 → SurfaceTexture / GL.
 * Wired in M2; skeleton ready for TOASTED keying.
 */
class MediaBus(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var surface: Surface? = null

    val audioSessionId: Int
        get() = player.audioSessionId

    fun attachSurfaceTexture(st: SurfaceTexture) {
        surface?.release()
        surface = Surface(st)
        player.setVideoSurface(surface)
    }

    fun playUri(uri: String) {
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun stop() {
        player.stop()
    }

    fun release() {
        player.release()
        surface?.release()
        surface = null
    }
}
