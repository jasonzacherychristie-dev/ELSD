package com.elsd.audio

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Turns spectrum / magnitude bins into smoothed bass–mid–high + onset ("beat").
 * MIT. Used for sound-activated hallucinations (PULSE / LSD drive).
 *
 * Feed either:
 *  - Android Visualizer FFT magnitudes, or
 *  - magnitudes from a future in-repo FFT on AudioRecord PCM.
 */
class BandEnergy(
    private val attack: Float = 0.45f,
    private val release: Float = 0.08f,
) {
    var bass: Float = 0f
        private set
    var mid: Float = 0f
        private set
    var high: Float = 0f
        private set
    var rms: Float = 0f
        private set
    /** 0–1 spike on spectral flux / energy jump. */
    var beat: Float = 0f
        private set

    private var prevFlux = 0f

    /**
     * @param mags linear magnitudes for bins 0..n-1 (DC..Nyquist-ish)
     */
    fun pushMagnitudes(mags: FloatArray) {
        if (mags.isEmpty()) return
        val n = mags.size
        val bEnd = max(2, n / 10)
        val mEnd = max(bEnd + 1, n / 3)

        var b = 0f
        var m = 0f
        var h = 0f
        var e = 0f
        for (i in 1 until n) {
            val v = mags[i]
            e += v * v
            when {
                i < bEnd -> b += v
                i < mEnd -> m += v
                else -> h += v
            }
        }
        val nb = (bEnd - 1).coerceAtLeast(1)
        val nm = (mEnd - bEnd).coerceAtLeast(1)
        val nh = (n - mEnd).coerceAtLeast(1)
        b = compress(b / nb)
        m = compress(m / nm)
        h = compress(h / nh)
        val r = compress(sqrt(e / n))

        bass = smooth(bass, b)
        mid = smooth(mid, m)
        high = smooth(high, h)
        rms = smooth(rms, r)

        val flux = max(0f, r - prevFlux)
        prevFlux = r * 0.85f + prevFlux * 0.15f
        val onset = compress(flux * 4f)
        beat = max(onset, beat * 0.75f)
    }

    /**
     * Android [android.media.audiofx.Visualizer] getFft() layout:
     * byte[0]=RF, byte[1]=RF?, then real/imag pairs — we use magnitude of pairs.
     */
    fun pushVisualizerFft(fft: ByteArray) {
        if (fft.size < 4) return
        val n = fft.size / 2
        val mags = FloatArray(n)
        // index 0 is DC-ish; pairs at 2,3 / 4,5 ...
        mags[0] = fft[0].toInt().and(0xFF) / 128f
        var mi = 1
        var i = 2
        while (i + 1 < fft.size && mi < n) {
            val re = fft[i].toInt()
            val im = fft[i + 1].toInt()
            mags[mi] = sqrt((re * re + im * im).toFloat()) / 128f
            mi++
            i += 2
        }
        pushMagnitudes(mags)
    }

    fun pushWaveform(wave: ByteArray) {
        if (wave.isEmpty()) return
        var e = 0f
        for (b in wave) {
            val c = (b.toInt() and 0xFF) - 128
            e += c * c
        }
        val r = compress(sqrt(e / wave.size) / 128f)
        rms = smooth(rms, r)
        bass = smooth(bass, r)
        val flux = max(0f, r - prevFlux)
        prevFlux = r
        beat = max(compress(flux * 5f), beat * 0.7f)
    }

    private fun smooth(prev: Float, next: Float): Float {
        val a = if (next > prev) attack else release
        return prev + (next - prev) * a
    }

    private fun compress(x: Float): Float {
        val v = max(0f, x)
        return min(1f, ln(1f + 6f * v) / ln(7f))
    }
}
