package com.elsd.board

/**
 * 1.0 catalog — **visuals only**.
 * ELSD · ART · CINEMA · PALETTE · MOOD
 * Plate/stream/key/ears → 2.0 (not in catalog1_0).
 * DeepDream → 3.0.
 */
enum class EffectFamily {
    ELSD,
    ART,
    CINEMA,
    PALETTE,
    MOOD,
}

enum class EffectId(
    val catalogName: String,
    val family: EffectFamily,
    val label: String,
) {
    // —— ELSD (trip) ——
    TRAIL("trail", EffectFamily.ELSD, "TRAIL"),
    HUE("hue", EffectFamily.ELSD, "HUE"),
    SPLIT("split", EffectFamily.ELSD, "SPLIT"),
    KALEIDO("kaleido", EffectFamily.ELSD, "KALEIDO"),
    MELT("melt", EffectFamily.ELSD, "MELT"),
    /** Stare longer → morph budget (see STILLNESS_MORPH.md). */
    STILLNESS_MORPH("stillness_morph", EffectFamily.ELSD, "STILLNESS MORPH"),

    // —— ART styles ——
    GOGH("gogh", EffectFamily.ART, "GOGH"),
    MONET("monet", EffectFamily.ART, "MONET"),
    SKETCH("sketch", EffectFamily.ART, "SKETCH"),
    COMIC("comic", EffectFamily.ART, "COMIC"),
    CARTOON("cartoon", EffectFamily.ART, "CARTOON"),
    HYPERREAL("hyperreal", EffectFamily.ART, "HYPERREAL"),
    MELT_PAINT("melt_paint", EffectFamily.ART, "MELT PAINT"),

    // —— CINEMA styles (noir-inspired grades + eras + directors) ——
    NOIR("noir", EffectFamily.CINEMA, "NOIR"),
    NEON("neon", EffectFamily.CINEMA, "NEON"),
    BLEACH("bleach", EffectFamily.CINEMA, "BLEACH"),
    TEAL_ORANGE("teal_orange", EffectFamily.CINEMA, "TEAL ORANGE"),
    ANAMORPHIC("anamorphic", EffectFamily.CINEMA, "ANAMORPHIC"),
    SOFT_GLOW("soft_glow", EffectFamily.CINEMA, "SOFT GLOW"),
    TECHNICOLOR("technicolor", EffectFamily.CINEMA, "TECHNICOLOR"),
    SUSPIRIA("suspiria", EffectFamily.CINEMA, "SUSPIRIA"),
    SILENT_ERA("silent_era", EffectFamily.CINEMA, "SILENT ERA"),
    EXPRESSIONIST("expressionist", EffectFamily.CINEMA, "EXPRESSIONIST"),
    GIALLO("giallo", EffectFamily.CINEMA, "GIALLO"),
    GOLDEN_AGE("golden_age", EffectFamily.CINEMA, "GOLDEN AGE"),

    // —— PALETTE (analog / digital medium) ——
    ANALOG_FILM("analog_film", EffectFamily.PALETTE, "ANALOG FILM"),
    ANALOG_VHS("analog_vhs", EffectFamily.PALETTE, "ANALOG VHS"),
    DIGITAL_CLEAN("digital_clean", EffectFamily.PALETTE, "DIGITAL CLEAN"),
    DIGITAL_HARSH("digital_harsh", EffectFamily.PALETTE, "DIGITAL HARSH"),
    POLAROID("polaroid", EffectFamily.PALETTE, "POLAROID"),

    // —— MOOD (single overall toggle) ——
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
