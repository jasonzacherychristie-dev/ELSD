package com.elsd.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Mic → band energy for live-room hallucinations (no plate required).
 * Small in-repo radix-2 FFT (MIT) — avoids GPL analysis libs.
 */
class MicEnergySource(
    private val bands: BandEnergy = BandEnergy(),
    private val sampleRate: Int = 22050,
    private val fftSize: Int = 512,
) {
    private var record: AudioRecord? = null
    @Volatile private var running = false
    private var worker: Thread? = null

    val energy: BandEnergy get() = bands

    @SuppressLint("MissingPermission")
    fun start() {
        if (running) return
        val minBuf = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        if (minBuf <= 0) {
            Log.e(TAG, "AudioRecord buffer unavailable")
            return
        }
        val rec = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            maxOf(minBuf, fftSize * 4),
        )
        if (rec.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord not initialized")
            rec.release()
            return
        }
        record = rec
        running = true
        rec.startRecording()
        worker = thread(name = "elsd-mic-fft", isDaemon = true) {
            val pcm = ShortArray(fftSize)
            val re = FloatArray(fftSize)
            val im = FloatArray(fftSize)
            val mags = FloatArray(fftSize / 2)
            while (running) {
                val n = rec.read(pcm, 0, fftSize)
                if (n < fftSize) continue
                // Hann window
                var i = 0
                while (i < fftSize) {
                    val w = (0.5f - 0.5f * cos(2.0 * PI * i / (fftSize - 1))).toFloat()
                    re[i] = pcm[i] / 32768f * w
                    im[i] = 0f
                    i++
                }
                fftInPlace(re, im)
                i = 0
                while (i < mags.size) {
                    mags[i] = sqrt(re[i] * re[i] + im[i] * im[i])
                    i++
                }
                bands.pushMagnitudes(mags)
            }
        }
        Log.i(TAG, "Mic energy running")
    }

    fun stop() {
        running = false
        try {
            worker?.join(500)
        } catch (_: Exception) {
        }
        worker = null
        try {
            record?.stop()
            record?.release()
        } catch (_: Exception) {
        }
        record = null
    }

    companion object {
        private const val TAG = "ELSD.Mic"

        /** In-place radix-2 Cooley–Tukey (n power of two). */
        fun fftInPlace(re: FloatArray, im: FloatArray) {
            val n = re.size
            var j = 0
            for (i in 1 until n) {
                var bit = n shr 1
                while (j and bit != 0) {
                    j = j xor bit
                    bit = bit shr 1
                }
                j = j xor bit
                if (i < j) {
                    val tr = re[i]; re[i] = re[j]; re[j] = tr
                    val ti = im[i]; im[i] = im[j]; im[j] = ti
                }
            }
            var len = 2
            while (len <= n) {
                val ang = -2.0 * PI / len
                val wlenRe = cos(ang).toFloat()
                val wlenIm = sin(ang).toFloat()
                var i = 0
                while (i < n) {
                    var wRe = 1f
                    var wIm = 0f
                    for (k in 0 until len / 2) {
                        val uRe = re[i + k]
                        val uIm = im[i + k]
                        val vRe = re[i + k + len / 2] * wRe - im[i + k + len / 2] * wIm
                        val vIm = re[i + k + len / 2] * wIm + im[i + k + len / 2] * wRe
                        re[i + k] = uRe + vRe
                        im[i + k] = uIm + vIm
                        re[i + k + len / 2] = uRe - vRe
                        im[i + k + len / 2] = uIm - vIm
                        val nWRe = wRe * wlenRe - wIm * wlenIm
                        wIm = wRe * wlenIm + wIm * wlenRe
                        wRe = nWRe
                    }
                    i += len
                }
                len = len shl 1
            }
        }
    }
}
