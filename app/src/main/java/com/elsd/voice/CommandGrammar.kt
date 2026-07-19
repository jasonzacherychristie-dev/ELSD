package com.elsd.voice

import com.elsd.bounce.BounceMode

/**
 * Spoken T-bar — parse free text (system SpeechRecognizer or Vosk) into commands.
 */
object CommandGrammar {

    fun parse(raw: String): Command {
        val t = raw.lowercase().trim()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (t.isEmpty()) return Command.Unknown(raw)

        // Safety first
        if (t.matches(Regex(".*(clear|sober|dry out|come back).*"))) {
            return Command.SafetyClear
        }
        if (t.matches(Regex(".*(stop now|stop|emergency).*"))) {
            return Command.SafetyStop
        }

        // Amy / Bounce presence
        if (t.contains("mute amy") || t.contains("mute bounce") || t == "quiet" ||
            t.contains("bounce off") || t.contains("amy off") || t.contains("go away amy")
        ) {
            return Command.BounceModeSet(BounceMode.MUTED)
        }
        if (t.contains("amy on") || t.contains("hey amy") || t.contains("show amy") ||
            t.contains("bounce on") || t.contains("hey bounce") || t.contains("show bounce")
        ) {
            return Command.BounceModeSet(BounceMode.FLAT)
        }
        if (t.contains("amy 3d") || t.contains("bounce 3d") || t.contains("bounce spatial") ||
            t.contains("bounce three") || t.contains("amy spatial")
        ) {
            return Command.BounceModeSet(BounceMode.SPATIAL)
        }
        if (t.contains("amy flat") || t.contains("bounce flat") || t.contains("bounce 2d")) {
            return Command.BounceModeSet(BounceMode.FLAT)
        }

        if (t.contains("no jokes") || t.contains("serious")) {
            return Command.Jokes(false)
        }
        if (t.contains("fun mode") || t.contains("jokes on")) {
            return Command.Jokes(true)
        }

        // Wet / TOASTED
        if (t == "toasted" || t.contains("toast it") || t.contains("full wet")) {
            return Command.Toasted
        }
        if (t == "dry" || t.contains("all dry")) {
            return Command.SetWet(0f)
        }
        if (t.contains("half wet") || t.contains("half")) {
            return Command.SetWet(0.5f)
        }
        if (t.contains("wetter") || t.contains("stronger") || t.contains("more wet")) {
            return Command.SetWet(0.75f)
        }
        if (t.contains("softer") || t.contains("less wet")) {
            return Command.SetWet(0.25f)
        }

        // Key
        if (t.contains("key luma") || t.contains("luma key") || t.contains("key brightness")) {
            return Command.KeyLuma
        }
        if (t.contains("key chroma") || t.contains("chroma key") || t.contains("key green") || t.contains("key blue")) {
            return Command.KeyChroma
        }
        if (t.contains("key off") || t.contains("no key")) {
            return Command.KeyOff
        }

        // Spatial audio hallucination (ears) — before generic "hallucinate"
        if (t.contains("ears dry") || t.contains("dry ears") || t.contains("spatial off") ||
            t.contains("no spatial")
        ) {
            return Command.EarsDry
        }
        if (t.contains("hallucinate ears") || t.contains("wet ears") || t.contains("spatial on") ||
            t.contains("ear trip")
        ) {
            return Command.Spatial("hallucinate_ears")
        }
        if (t.contains("something behind") || t.contains("behind me")) {
            return Command.Spatial("behind")
        }
        if (t.contains("ghost choir")) return Command.Spatial("ghost_choir")
        if (t.contains("bass crawl")) return Command.Spatial("bass_crawl")
        if (t.contains("amy whisper") || t.contains("whisper amy")) {
            return Command.Spatial("amy_whisper")
        }
        if (t.contains("world lock audio") || t.contains("world lock ears")) {
            return Command.Spatial("world_lock")
        }
        if (t.contains("insect ears") || t == "insect") return Command.Spatial("insect")
        if (t.contains("cathedral")) return Command.Spatial("cathedral")
        if (t.contains("orbit") || t.contains("sounds spin") || t.contains("spin sound")) {
            return Command.Spatial("orbit")
        }

        // Pulse / visual hallucinations
        if (t.contains("hallucinate") || t.contains("sound trip") || t.contains("audio trip")) {
            return Command.PulseOn
        }
        if (t.contains("city pulse") || t.contains("pulse on") || t == "pulse" || t.contains("heartbeat")) {
            return Command.PulseOn
        }
        if (t.contains("pulse off") || t.contains("no pulse")) {
            return Command.PulseOff
        }
        if (t.contains("full hallucination") || t.contains("full trip")) {
            return Command.Preset("full_hallucination")
        }

        // Paint
        listOf("gogh", "monet", "noir", "neon", "sketch", "melt").forEach { id ->
            if (t.contains(id)) return Command.Paint(id)
        }
        if (t.contains("no paint") || t.contains("paint off")) {
            return Command.Paint("none")
        }

        // LSD
        when {
            t.contains("trail") || t.contains("trails") -> return Command.Lsd("trail")
            t.contains("hue") || t.contains("color melt") -> return Command.Lsd("hue")
            t.contains("split") || t.contains("chroma split") -> return Command.Lsd("split")
            t.contains("kaleido") || t.contains("kaleidoscope") -> return Command.Lsd("kaleido")
            t.contains("lsd melt") -> return Command.Lsd("melt")
            t.contains("no trip") || t.contains("lsd off") -> return Command.Lsd("none")
        }

        // Presets
        when {
            t.contains("window tv") || t.contains("window television") ->
                return Command.Preset("window_tv")
            t.contains("gogh walk") || t.contains("van gogh walk") ->
                return Command.Preset("gogh_walk")
            t.contains("sky cinema") || t.contains("sky film") ->
                return Command.Preset("sky_cinema")
            t.contains("quiet toasted") || t.contains("quiet trip") ->
                return Command.Preset("quiet_toasted")
        }

        return Command.Unknown(raw)
    }
}
