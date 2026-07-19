package com.elsd.vision

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Estimates how still the device + scene are, and how long that has held.
 * Feeds MorphBudget — "stare longer → spend more cycles morphing."
 *
 * Lightweight: gyro magnitude + downscaled frame difference (no ML).
 */
class StillnessTracker(
    private val enterThreshold: Float = 0.55f,
    private val exitThreshold: Float = 0.35f,
) {
    /** 0..1 combined still score. */
    var stillScore: Float = 0f
        private set

    /** Seconds above enter threshold (with hysteresis). */
    var holdSec: Float = 0f
        private set

    var userStill: Float = 0f
        private set
    var sceneStill: Float = 0f
        private set

    private var holding = false
    private var prevLuma: FloatArray? = null
    private val gridW = 32
    private val gridH = 18

    /** Call when gyro/accel samples arrive. motionMag: rad/s or arbitrary positive motion energy. */
    fun onDeviceMotion(motionMag: Float, dt: Float) {
        // Low motion → high still
        val m = motionMag.coerceAtLeast(0f)
        val instant = exp(-m * 3.5f).toFloat().coerceIn(0f, 1f)
        userStill = ema(userStill, instant, if (instant > userStill) 0.35f else 0.2f)
        tickHold(dt)
    }

    /**
     * Downscaled luma samples length = gridW * gridH, values 0..1.
     * Center-weighted: focus proxy without eye tracking.
     */
    fun onLumaGrid(luma: FloatArray, dt: Float) {
        if (luma.size < gridW * gridH) return
        val prev = prevLuma
        if (prev == null || prev.size != luma.size) {
            prevLuma = luma.copyOf()
            sceneStill = 0.5f
            tickHold(dt)
            return
        }
        var edgeEnergy = 0f
        var centerEnergy = 0f
        var edgeW = 0f
        var centerW = 0f
        val cx = (gridW - 1) * 0.5f
        val cy = (gridH - 1) * 0.5f
        for (y in 0 until gridH) {
            for (x in 0 until gridW) {
                val i = y * gridW + x
                val d = abs(luma[i] - prev[i])
                val dx = (x - cx) / cx
                val dy = (y - cy) / cy
                val dist = sqrt(dx * dx + dy * dy)
                val center = (1f - min(1f, dist)).coerceIn(0f, 1f)
                centerEnergy += d * (0.5f + center)
                centerW += 0.5f + center
                edgeEnergy += d * (0.5f + (1f - center))
                edgeW += 0.5f + (1f - center)
            }
        }
        prevLuma = luma.copyOf()
        val meanCenter = if (centerW > 0f) centerEnergy / centerW else 0f
        val meanEdge = if (edgeW > 0f) edgeEnergy / edgeW else 0f
        // Still if center is calm (focused still objects); periphery less critical
        val motion = meanCenter * 0.75f + meanEdge * 0.25f
        val instant = exp(-motion * 28f).toFloat().coerceIn(0f, 1f)
        sceneStill = ema(sceneStill, instant, if (instant > sceneStill) 0.4f else 0.25f)
        tickHold(dt)
    }

    private fun tickHold(dt: Float) {
        stillScore = (userStill * 0.45f + sceneStill * 0.55f).coerceIn(0f, 1f)
        if (!holding && stillScore >= enterThreshold) holding = true
        if (holding && stillScore < exitThreshold) holding = false
        holdSec = if (holding) holdSec + dt else max(0f, holdSec - dt * 2.5f)
    }

    fun reset() {
        holdSec = 0f
        holding = false
        stillScore = 0f
        userStill = 0f
        sceneStill = 0f
        prevLuma = null
    }

    private fun ema(prev: Float, x: Float, a: Float) = prev + (x - prev) * a

    companion object {
        const val GRID_W = 32
        const val GRID_H = 18
    }
}
