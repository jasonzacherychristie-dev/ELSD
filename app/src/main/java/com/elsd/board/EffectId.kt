package com.elsd.board

/**
 * 1.0 catalog — visuals only.
 * User banks: DESK · ART · FILM · PSYCHEDELIC · PALETTE · MOOD
 * See docs/design/FEATURES_1_0.md
 */
enum class EffectFamily {
    /** Canonical live-desk / Toaster-class FX */
    DESK,
    ART,
    /** Film / cinema styles */
    FILM,
    /** Trip geometry, fractals */
    PSYCHEDELIC,
    PALETTE,
    MOOD,
}

enum class EffectId(
    val catalogName: String,
    val family: EffectFamily,
    val label: String,
) {
    // —— DESK (live-desk / Toaster spirit) ——
    TRAIL("trail", EffectFamily.DESK, "TRAIL"),
    HUE("hue", EffectFamily.DESK, "HUE"),
    SPLIT("split", EffectFamily.DESK, "SPLIT"),
    NEGATIVE("negative", EffectFamily.DESK, "NEGATIVE"),
    POSTERIZE("posterize", EffectFamily.DESK, "POSTERIZE"),
    SOLARIZE("solarize", EffectFamily.DESK, "SOLARIZE"),
    MIRROR("mirror", EffectFamily.DESK, "MIRROR"),
    MIRROR_V("mirror_v", EffectFamily.DESK, "MIRROR V"),
    MIRROR_QUAD("mirror_quad", EffectFamily.DESK, "MIRROR QUAD"),

    // —— ART ——
    GOGH("gogh", EffectFamily.ART, "GOGH"),
    MONET("monet", EffectFamily.ART, "MONET"),
    SKETCH("sketch", EffectFamily.ART, "SKETCH"),
    COMIC("comic", EffectFamily.ART, "COMIC"),
    CARTOON("cartoon", EffectFamily.ART, "CARTOON"),
    HYPERREAL("hyperreal", EffectFamily.ART, "HYPERREAL"),
    MELT_PAINT("melt_paint", EffectFamily.ART, "MELT PAINT"),

    // —— FILM ——
    NOIR("noir", EffectFamily.FILM, "NOIR"),
    NEON("neon", EffectFamily.FILM, "NEON"),
    BLEACH("bleach", EffectFamily.FILM, "BLEACH"),
    TEAL_ORANGE("teal_orange", EffectFamily.FILM, "TEAL ORANGE"),
    ANAMORPHIC("anamorphic", EffectFamily.FILM, "ANAMORPHIC"),
    SOFT_GLOW("soft_glow", EffectFamily.FILM, "SOFT GLOW"),
    TECHNICOLOR("technicolor", EffectFamily.FILM, "TECHNICOLOR"),
    SUSPIRIA("suspiria", EffectFamily.FILM, "SUSPIRIA"),
    SILENT_ERA("silent_era", EffectFamily.FILM, "SILENT ERA"),
    EXPRESSIONIST("expressionist", EffectFamily.FILM, "EXPRESSIONIST"),
    GIALLO("giallo", EffectFamily.FILM, "GIALLO"),
    GOLDEN_AGE("golden_age", EffectFamily.FILM, "GOLDEN AGE"),

    // —— PSYCHEDELIC (mirrors / kaleidoscope family + trip) ——
    KALEIDO("kaleido", EffectFamily.PSYCHEDELIC, "KALEIDO"),
    KALEIDO_4("kaleido_4", EffectFamily.PSYCHEDELIC, "KALEIDO 4"),
    KALEIDO_8("kaleido_8", EffectFamily.PSYCHEDELIC, "KALEIDO 8"),
    KALEIDO_12("kaleido_12", EffectFamily.PSYCHEDELIC, "KALEIDO 12"),
    KALEIDO_SPIN("kaleido_spin", EffectFamily.PSYCHEDELIC, "KALEIDO SPIN"),
    TRI_MIRROR("tri_mirror", EffectFamily.PSYCHEDELIC, "TRI MIRROR"),
    MELT("melt", EffectFamily.PSYCHEDELIC, "MELT"),
    MANDELBROT("mandelbrot", EffectFamily.PSYCHEDELIC, "MANDELBROT"),
    JULIA("julia", EffectFamily.PSYCHEDELIC, "JULIA"),
    STILLNESS_MORPH("stillness_morph", EffectFamily.PSYCHEDELIC, "STILLNESS MORPH"),

    // —— PALETTE ——
    ANALOG_FILM("analog_film", EffectFamily.PALETTE, "ANALOG FILM"),
    ANALOG_VHS("analog_vhs", EffectFamily.PALETTE, "ANALOG VHS"),
    DIGITAL_CLEAN("digital_clean", EffectFamily.PALETTE, "DIGITAL CLEAN"),
    DIGITAL_HARSH("digital_harsh", EffectFamily.PALETTE, "DIGITAL HARSH"),
    POLAROID("polaroid", EffectFamily.PALETTE, "POLAROID"),

    // —— MOOD ——
    MOOD_NEUTRAL("mood_neutral", EffectFamily.MOOD, "MOOD NEUTRAL"),
    MOOD_CALM("mood_calm", EffectFamily.MOOD, "MOOD CALM"),
    MOOD_WARM("mood_warm", EffectFamily.MOOD, "MOOD WARM"),
    MOOD_COLD("mood_cold", EffectFamily.MOOD, "MOOD COLD"),
    MOOD_NIGHT("mood_night", EffectFamily.MOOD, "MOOD NIGHT"),
    MOOD_FEVER("mood_fever", EffectFamily.MOOD, "MOOD FEVER"),
    MOOD_TOASTED("mood_toasted", EffectFamily.MOOD, "MOOD TOASTED"),
    ;

    companion object {
        fun fromCatalog(name: String): EffectId? {
            val n = name.trim().lowercase().replace(" ", "_")
            return entries.find {
                it.catalogName == n ||
                    it.label.replace(" ", "_").lowercase() == n ||
                    it.name.equals(name, true)
            }
        }

        fun catalog1_0(): List<EffectId> = entries.toList()

        fun moods(): List<EffectId> = entries.filter { it.family == EffectFamily.MOOD }
    }
}
