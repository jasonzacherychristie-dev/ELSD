package com.elsd.audio

/**
 * Spatial audio hallucination modes — wet ears, not a music player skin.
 * See docs/frameworks/SPATIAL_AUDIO.md
 */
enum class SpatialMode(val id: String) {
    OFF("off"),
    ORBIT("orbit"),
    BEHIND("behind"),
    CATHEDRAL("cathedral"),
    INSECT("insect"),
    GHOST_CHOIR("ghost_choir"),
    BASS_CRAWL("bass_crawl"),
    AMY_WHISPER("amy_whisper"),
    WORLD_LOCK("world_lock"),
    HALLUCINATE_EARS("hallucinate_ears"),
    ;

    companion object {
        fun fromId(id: String): SpatialMode =
            entries.find { it.id == id } ?: OFF
    }
}
