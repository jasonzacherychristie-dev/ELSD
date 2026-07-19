package com.elsd.bounce

/**
 * Cute, quick Amy actions — procedural now; Blender flipbook later.
 * Toggle all motion en masse via [BounceCursor.actionsEnabled].
 */
enum class AmyAction(
    val id: String,
    val durationSec: Float,
) {
    IDLE("idle", 0f),
    LEAN_IN("lean_in", 0.35f),
    LEAN_HOLD("lean_hold", 0f),
    LEAN_OUT("lean_out", 0.4f),
    /** Vertical hop / boing */
    BOUNCE("bounce", 0.45f),
    /** Roll along to a new rest spot */
    ROLL("roll", 0.55f),
    /** Pop out + pop in at a new place */
    TELEPORT("teleport", 0.4f),
    /** Small hop-step */
    HOP("hop", 0.28f),
    /** Spin in place (checkered blur feel) */
    SPIN("spin", 0.5f),
    /** Squash acknowledge */
    ACK("ack", 0.22f),
    /** Happy pop when toasted */
    POP("pop", 0.35f),
    ;

    companion object {
        fun fromId(s: String): AmyAction? =
            entries.find { it.id.equals(s, true) || it.name.equals(s, true) }
    }
}
