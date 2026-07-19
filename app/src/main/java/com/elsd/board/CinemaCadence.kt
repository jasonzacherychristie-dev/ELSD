package com.elsd.board

/**
 * Variable FPS as film language — cadence suggested by CINEMA styles.
 * Soft apply: presets and ADD EFFECT cinema set board targetFps unless user unlocked (0).
 */
object CinemaCadence {

    data class Suggestion(
        val fps: Int,
        val preferDroppedFrames: Boolean = true,
    )

    fun forEffect(id: EffectId): Suggestion? = when (id) {
        EffectId.SILENT_ERA -> Suggestion(12, true)
        EffectId.EXPRESSIONIST -> Suggestion(16, true)
        EffectId.GOLDEN_AGE,
        EffectId.TECHNICOLOR,
        EffectId.NOIR,
        EffectId.SUSPIRIA,
        EffectId.GIALLO,
        EffectId.BLEACH,
        EffectId.TEAL_ORANGE,
        -> Suggestion(24, true)
        EffectId.ANAMORPHIC,
        EffectId.SOFT_GLOW,
        -> Suggestion(24, true)
        EffectId.NEON -> Suggestion(30, true)
        else -> null
    }

    /**
     * @param hardLocked if true (user chose UNLOCK or explicit lock), do not override fps
     */
    fun applySoft(board: Switchboard, id: EffectId, hardLocked: Boolean = false) {
        if (hardLocked) return
        val s = forEffect(id) ?: return
        // Don't fight explicit unlock (0) if user set it this session — only if still defaulty
        // Soft rule: always set when adding cinema; unlock is targetFps==0 and user asked unlock
        if (board.targetFps == 0) return
        board.targetFps = s.fps
        if (s.preferDroppedFrames) board.allowDroppedFrames = true
    }
}
