package com.elsd.gl

/**
 * Framerate ceiling + intentional dropped frames.
 * Aesthetic cadence (hold last frame) and budget breathing room for heavy morph.
 *
 * @param targetFps 0 = unlocked (present every draw opportunity)
 * @param allowDroppedFrames if true, under load we may skip work and keep last present time
 */
class FramePacer(
    targetFps: Int = 30,
    var allowDroppedFrames: Boolean = true,
    var navBoost: Boolean = true,
) {
    /** 0 = unlock */
    var targetFps: Int = targetFps
        set(value) {
            field = if (value <= 0) 0 else value.coerceIn(MIN_FPS, MAX_FPS)
        }

    private var lastPresentNs: Long = 0L
    private var framesPresented: Long = 0L
    private var framesSkipped: Long = 0L

    /**
     * @param nowNs System.nanoTime()
     * @param stillScore 0..1 — when low (moving), navBoost can raise effective fps
     * @param overloaded hint from renderer that last frame was expensive
     * @return true if this tick should run full graph + swap
     */
    fun shouldPresent(nowNs: Long, stillScore: Float = 1f, overloaded: Boolean = false): Boolean {
        val fps = effectiveFps(stillScore)
        if (fps <= 0) {
            framesPresented++
            lastPresentNs = nowNs
            return true
        }
        val intervalNs = 1_000_000_000L / fps
        val elapsed = nowNs - lastPresentNs
        if (elapsed < intervalNs) {
            // Too soon for aesthetic cadence
            framesSkipped++
            return false
        }
        // Budget drops: if overloaded and drops allowed, occasionally stretch interval
        if (allowDroppedFrames && overloaded && elapsed < intervalNs * 3 / 2) {
            framesSkipped++
            return false
        }
        lastPresentNs = nowNs
        framesPresented++
        return true
    }

    fun effectiveFps(stillScore: Float): Int {
        if (targetFps <= 0) return 0
        if (navBoost && stillScore < 0.35f) {
            // Moving — prefer clearer world
            return maxOf(targetFps, 30).coerceAtMost(MAX_FPS)
        }
        return targetFps
    }

    /** Headroom factor 0..1 for MorphBudget (lower fps → more budget). */
    fun morphHeadroom(): Float {
        if (targetFps <= 0) return if (allowDroppedFrames) 0.35f else 0.15f
        val base = (60f - targetFps.coerceAtMost(60)) / 48f
        return (base + if (allowDroppedFrames) 0.25f else 0f).coerceIn(0f, 1f)
    }

    fun statusLine(): String {
        val fps = if (targetFps <= 0) "UNLOCK" else "${targetFps}fps"
        val drop = if (allowDroppedFrames) "DROP ON" else "DROP OFF"
        return "FRAME $fps $drop (ok=$framesPresented skip=$framesSkipped)"
    }

    fun resetStats() {
        framesPresented = 0
        framesSkipped = 0
    }

    companion object {
        const val MIN_FPS = 8
        const val MAX_FPS = 120

        val PRESET_LIVE = 60
        val PRESET_BROADCAST = 30
        val PRESET_CINEMA = 24
        val PRESET_SILENT = 12
        val PRESET_STARE = 15
    }
}
