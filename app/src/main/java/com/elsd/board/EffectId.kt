package com.elsd.board

/**
 * Catalog of switchboard effects. DeepDream is 3.0 — not listed for 1.0 ADD EFFECT.
 */
enum class EffectFamily {
    KEY, PULSE, PAINT, LSD, EARS
}

enum class EffectId(
    val catalogName: String,
    val family: EffectFamily,
    val label: String,
) {
    KEY_LUMA("key_luma", EffectFamily.KEY, "KEY LUMA"),
    KEY_CHROMA("key_chroma", EffectFamily.KEY, "KEY CHROMA"),
    CITY_PULSE("city_pulse", EffectFamily.PULSE, "CITY PULSE"),
    GOGH("gogh", EffectFamily.PAINT, "GOGH"),
    MONET("monet", EffectFamily.PAINT, "MONET"),
    NOIR("noir", EffectFamily.PAINT, "NOIR"),
    NEON("neon", EffectFamily.PAINT, "NEON"),
    SKETCH("sketch", EffectFamily.PAINT, "SKETCH"),
    MELT_PAINT("melt_paint", EffectFamily.PAINT, "MELT PAINT"),
    TRAIL("trail", EffectFamily.LSD, "TRAIL"),
    HUE("hue", EffectFamily.LSD, "HUE"),
    SPLIT("split", EffectFamily.LSD, "SPLIT"),
    KALEIDO("kaleido", EffectFamily.LSD, "KALEIDO"),
    MELT("melt", EffectFamily.LSD, "MELT"),
    ORBIT("orbit", EffectFamily.EARS, "ORBIT"),
    BEHIND("behind", EffectFamily.EARS, "BEHIND"),
    CATHEDRAL("cathedral", EffectFamily.EARS, "CATHEDRAL"),
    BASS_CRAWL("bass_crawl", EffectFamily.EARS, "BASS CRAWL"),
    HALLUCINATE_EARS("hallucinate_ears", EffectFamily.EARS, "HALLUCINATE EARS"),
    ;

    companion object {
        fun fromCatalog(name: String): EffectId? =
            entries.find {
                it.catalogName.equals(name, true) ||
                    it.label.replace(" ", "_").equals(name, true) ||
                    it.name.equals(name, true)
            }

        /** 1.0 ADD EFFECT menu — no neural dream. */
        fun catalog1_0(): List<EffectId> = entries.toList()
    }
}
