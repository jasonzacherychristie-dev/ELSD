package com.elsd.audio

import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spatial audio hallucination engine (M2.5 skeleton).
 *
 * Head-locked orbit / rear bias / wet amount driven by [BandEnergy] and mode.
 * Output path (AudioTrack / Media3 processor / Resonance) plugs in later;
 * this class owns the *control math* and safety dry-out.
 *
 * MIT — ELSD.
 */
class SpatialHallucinationEngine {

    @Volatile var mode: SpatialMode = SpatialMode.OFF
        private set

    /** 0 = dry ears, 1 = full hallucination. Independent of visual wet unless coupled. */
    @Volatile var earWet: Float = 0f
        private set

    @Volatile var worldLock: Boolean = false
        private set

    /** Head-locked pan angle radians (orbit LFO + bass). */
    @Volatile var panAngle: Float = 0f
        private set

    /** -1 rear … +1 front emphasis (simple stand-in for HRTF blend). */
    @Volatile var frontBack: Float = 0f
        private set

    /** Extra reverb send 0..1 (cathedral). */
    @Volatile var reverbSend: Float = 0f
        private set

    private var time = 0f
    private var yaw = 0f

    fun setMode(m: SpatialMode) {
        mode = m
        when (m) {
            SpatialMode.OFF -> {
                earWet = 0f
                worldLock = false
            }
            SpatialMode.HALLUCINATE_EARS -> {
                earWet = 0.7f
                worldLock = false
                // mild orbit + behind as macro
            }
            SpatialMode.WORLD_LOCK -> {
                worldLock = true
                earWet = earWet.coerceAtLeast(0.5f)
            }
            SpatialMode.ORBIT,
            SpatialMode.BEHIND,
            SpatialMode.CATHEDRAL,
            SpatialMode.INSECT,
            SpatialMode.GHOST_CHOIR,
            SpatialMode.BASS_CRAWL,
            SpatialMode.AMY_WHISPER,
            -> {
                earWet = earWet.coerceAtLeast(0.55f)
            }
        }
        Log.i(TAG, "spatial mode=$m earWet=$earWet")
    }

    fun setEarWet(v: Float) {
        earWet = v.coerceIn(0f, 1f)
        if (earWet <= 0.01f) mode = SpatialMode.OFF
    }

    /** Safety: clear / sober / stop */
    fun dryOut(hard: Boolean) {
        mode = SpatialMode.OFF
        earWet = 0f
        reverbSend = 0f
        frontBack = 0f
        if (hard) {
            panAngle = 0f
            worldLock = false
        }
    }

    fun setPoseYaw(yawRad: Float) {
        yaw = yawRad
    }

    /**
     * Advance control state. Call each audio or render tick.
     * @param bands from Visualizer / mic
     * @param dt seconds
     */
    fun tick(bands: BandEnergy, dt: Float) {
        time += dt
        if (mode == SpatialMode.OFF || earWet < 0.01f) {
            // ease toward dry
            panAngle *= 0.9f
            frontBack *= 0.9f
            reverbSend *= 0.9f
            return
        }

        val bass = bands.bass
        val beat = bands.beat
        val rate = when (mode) {
            SpatialMode.ORBIT, SpatialMode.HALLUCINATE_EARS -> 0.7f + bass * 2.2f
            SpatialMode.BASS_CRAWL -> 0.15f + bass * 0.5f
            SpatialMode.INSECT -> 2.5f + bands.high * 3f
            else -> 0.4f
        }

        var angle = panAngle + dt * rate
        if (worldLock || mode == SpatialMode.WORLD_LOCK || mode == SpatialMode.BASS_CRAWL) {
            // World-ish: subtract head yaw so field sticks to room
            angle = angle - yaw
        }
        panAngle = angle

        frontBack = when (mode) {
            SpatialMode.BEHIND -> -0.75f - 0.2f * beat
            SpatialMode.AMY_WHISPER -> 0.85f // near face / lean-in cheek
            SpatialMode.HALLUCINATE_EARS -> -0.25f + 0.4f * sin(time * 0.5f)
            SpatialMode.INSECT -> 0.3f * sin(time * 8f)
            else -> 0.15f * cos(time * 0.3f)
        }

        reverbSend = when (mode) {
            SpatialMode.CATHEDRAL -> 0.55f + 0.35f * earWet
            SpatialMode.GHOST_CHOIR -> 0.4f + 0.2f * bands.mid
            SpatialMode.HALLUCINATE_EARS -> 0.25f * earWet
            else -> 0.08f * earWet
        }.coerceIn(0f, 1f)
    }

    /**
     * Simple stereo gains for a head-locked source (pre-HRTF stand-in).
     * Left/right in 0..1; apply to plate or generator until Resonance lands.
     */
    fun stereoGains(): Pair<Float, Float> {
        if (earWet < 0.01f) return 1f to 1f
        val pan = sin(panAngle) * earWet // -1..1
        val l = ((1f - pan) * 0.5f + 0.5f).coerceIn(0.15f, 1f)
        val r = ((1f + pan) * 0.5f + 0.5f).coerceIn(0.15f, 1f)
        // Rear bias: duck a bit and we’ll EQ later
        val rear = ((-frontBack).coerceIn(0f, 1f))
        val atten = 1f - 0.25f * rear * earWet
        return (l * atten) to (r * atten)
    }

    fun statusLine(): String =
        "ears=${mode.id} wet=${"%.2f".format(earWet)} pan=${"%.1f".format(panAngle)} " +
            "fb=${"%.2f".format(frontBack)} verb=${"%.2f".format(reverbSend)}"

    companion object {
        private const val TAG = "ELSD.Spatial"
    }
}
