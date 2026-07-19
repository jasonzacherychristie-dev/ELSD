package com.elsd.vision

/**
 * Maps stillness hold time → morph investment level.
 * L0 live … L3 strike (GLSL) … L4 dream (neural, 3.0).
 */
class MorphBudget(
    /** Master switch — ADD EFFECT STILLNESS / preference. */
    var enabled: Boolean = true,
    /** Thermal/fps cap 0..4. */
    var maxLevel: Int = 3,
) {
    enum class Level(val index: Int) {
        L0_LIVE(0),
        L1_SETTLE(1),
        L2_STRUCTURE(2),
        L3_STRIKE(3),
        L4_DREAM(4),
    }

    var level: Level = Level.L0_LIVE
        private set

    /** 0..1 within-level blend for smooth uniforms. */
    var levelBlend: Float = 0f
        private set

    /** Extra wet boost for ELSD/ART from staring. */
    var morphWetBoost: Float = 0f
        private set

    /** Suggested extra FBO passes (0..N). */
    var extraPasses: Int = 0
        private set

    fun update(holdSec: Float, stillScore: Float) {
        if (!enabled || stillScore < 0.2f) {
            level = Level.L0_LIVE
            levelBlend = 0f
            morphWetBoost = 0f
            extraPasses = 0
            return
        }
        // Thresholds (seconds of hold)
        val raw = when {
            holdSec < 0.5f -> 0f
            holdSec < 2f -> (holdSec - 0.5f) / 1.5f // 0..1 → L1
            holdSec < 5f -> 1f + (holdSec - 2f) / 3f // 1..2 → L2
            holdSec < 15f -> 2f + (holdSec - 5f) / 10f // 2..3 → L3
            else -> 3f + ((holdSec - 15f) / 20f).coerceAtMost(1f) // 3..4 → L4
        }
        val capped = raw.coerceAtMost(maxLevel.toFloat())
        val idx = capped.toInt().coerceIn(0, 4)
        level = Level.entries[idx]
        levelBlend = (capped - idx).coerceIn(0f, 1f)
        morphWetBoost = (capped / 4f).coerceIn(0f, 1f) * stillScore
        extraPasses = when (level) {
            Level.L0_LIVE -> 0
            Level.L1_SETTLE -> 1
            Level.L2_STRUCTURE -> 2
            Level.L3_STRIKE -> 3
            Level.L4_DREAM -> 1 // neural async; light display path
        }
    }

    fun statusLine(): String =
        if (!enabled) "morph=off"
        else "morph=${level.name} +${"%.0f".format(morphWetBoost * 100)}% pass=$extraPasses"
}
