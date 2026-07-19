package com.elsd.board

import kotlin.random.Random

/**
 * RANDOM mode — builds a coherent random visual board.
 * Voice: "random", "surprise me", "roll the dice", "shuffle", "chaos".
 */
object RandomDesk {

    private val arts = listOf(
        EffectId.GOGH, EffectId.MONET, EffectId.SKETCH, EffectId.COMIC,
        EffectId.CARTOON, EffectId.HYPERREAL, EffectId.MELT_PAINT,
    )
    private val films = listOf(
        EffectId.NOIR, EffectId.NEON, EffectId.BLEACH, EffectId.TEAL_ORANGE,
        EffectId.ANAMORPHIC, EffectId.SOFT_GLOW, EffectId.TECHNICOLOR,
        EffectId.SUSPIRIA, EffectId.SILENT_ERA, EffectId.EXPRESSIONIST,
        EffectId.GIALLO, EffectId.GOLDEN_AGE,
    )
    private val psych = listOf(
        EffectId.KALEIDO, EffectId.KALEIDO_4, EffectId.KALEIDO_8, EffectId.KALEIDO_12,
        EffectId.KALEIDO_SPIN, EffectId.TRI_MIRROR, EffectId.MELT,
        EffectId.MANDELBROT, EffectId.JULIA,
    )
    private val deskToys = listOf(
        EffectId.HUE, EffectId.SPLIT, EffectId.NEGATIVE, EffectId.POSTERIZE,
        EffectId.MIRROR, EffectId.MIRROR_V, EffectId.MIRROR_QUAD,
    )
    private val palettes = listOf(
        EffectId.ANALOG_FILM, EffectId.ANALOG_VHS, EffectId.DIGITAL_CLEAN,
        EffectId.DIGITAL_HARSH, EffectId.POLAROID,
    )
    private val moods = listOf(
        EffectId.MOOD_CALM, EffectId.MOOD_WARM, EffectId.MOOD_COLD,
        EffectId.MOOD_NIGHT, EffectId.MOOD_FEVER, EffectId.MOOD_TOASTED,
    )

    /**
     * Clears non-essential layers and rolls a fresh board.
     * Always picks mood + palette; then randomly stacks art/film/psych/desk/trail.
     */
    fun roll(board: Switchboard, rng: Random = Random.Default): String {
        board.clearBoard()
        board.presetName = "random"
        board.framerateHardLocked = false
        board.globalWet = rng.nextDouble(0.55, 0.9).toFloat()
        board.allowDroppedFrames = true

        // Always: medium + weather
        board.addEffect(palettes.random(rng))
        board.addEffect(moods.random(rng))

        val bits = mutableListOf<String>()

        // Film OR art (sometimes both lightly)
        when (rng.nextInt(3)) {
            0 -> {
                val f = films.random(rng)
                board.addEffect(f)
                bits += f.label
                CinemaCadence.applySoft(board, f, false)
            }
            1 -> {
                val a = arts.random(rng)
                board.addEffect(a)
                bits += a.label
                board.targetFps = listOf(24, 30).random(rng)
            }
            else -> {
                val f = films.random(rng)
                val a = arts.random(rng)
                board.addEffect(f)
                board.addEffect(a)
                bits += f.label
                bits += a.label
                CinemaCadence.applySoft(board, f, false)
            }
        }

        // Psychedelic spice
        if (rng.nextFloat() < 0.55f) {
            val p = psych.random(rng)
            val layer = board.addEffect(p)
            if (p == EffectId.MANDELBROT || p == EffectId.JULIA) {
                layer.rate = listOf(0.5f, 0.85f, 1.2f, 1.8f).random(rng)
                board.fractalKeyMode = listOf(1, 2, 3).random(rng)
            }
            bits += p.label
            if (board.targetFps > 30 || board.targetFps == 0) {
                board.targetFps = 24
            }
        }

        // Desk toy
        if (rng.nextFloat() < 0.4f) {
            val d = deskToys.random(rng)
            board.addEffect(d)
            bits += d.label
        }

        // Trails often with psych / neon energy
        if (rng.nextFloat() < 0.5f) {
            board.addEffect(EffectId.TRAIL)
            bits += "TRAIL"
        }

        // Stillness sometimes
        if (rng.nextFloat() < 0.2f) {
            board.addEffect(EffectId.STILLNESS_MORPH)
            bits += "STILLNESS"
        }

        return "RANDOM: " + bits.joinToString(" · ")
    }
}
