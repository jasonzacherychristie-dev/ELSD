package com.elsd.audio

import android.media.audiofx.Visualizer
import android.util.Log

/**
 * Sound → hallucination drive from a media audio session (ExoPlayer).
 * Platform Visualizer API — no third-party license issues.
 */
class MediaVisualizerSource(
    private val bands: BandEnergy = BandEnergy(),
) {
    private var visualizer: Visualizer? = null

    val energy: BandEnergy get() = bands

    fun attach(audioSessionId: Int) {
        release()
        if (audioSessionId == 0) {
            Log.w(TAG, "audioSessionId=0; visualizer may be empty on some devices")
        }
        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer?,
                            waveform: ByteArray?,
                            samplingRate: Int,
                        ) {
                            if (waveform != null) bands.pushWaveform(waveform)
                        }

                        override fun onFftDataCapture(
                            visualizer: Visualizer?,
                            fft: ByteArray?,
                            samplingRate: Int,
                        ) {
                            if (fft != null) bands.pushVisualizerFft(fft)
                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true,
                    true,
                )
                enabled = true
            }
            Log.i(TAG, "Visualizer attached session=$audioSessionId")
        } catch (e: Exception) {
            Log.e(TAG, "Visualizer attach failed", e)
            visualizer = null
        }
    }

    fun release() {
        try {
            visualizer?.enabled = false
            visualizer?.release()
        } catch (_: Exception) {
        }
        visualizer = null
    }

    companion object {
        private const val TAG = "ELSD.Viz"
    }
}
