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

        // Framerate / dropped frames
        if (t.contains("unlock framerate") || t.contains("framerate off") || t.contains("unlock frame")) {
            return Command.UnlockFramerate
        }
        if (t.contains("enable dropped frames") || t.contains("dropped frames on") ||
            t.contains("allow dropped frames")
        ) {
            return Command.DroppedFrames(true)
        }
        if (t.contains("disable dropped frames") || t.contains("dropped frames off") ||
            t.contains("no dropped frames")
        ) {
            return Command.DroppedFrames(false)
        }
        Regex("(?:set framerate|framerate|frame rate|fps)\\s*(\\d+)").find(t)?.let { m ->
            return Command.SetFramerate(m.groupValues[1].toInt().coerceIn(8, 120))
        }
        when {
            t.contains("cinema fps") || t.contains("film fps") -> return Command.SetFramerate(24)
            t.contains("silent fps") || t.contains("hand crank") -> return Command.SetFramerate(12)
            t.contains("broadcast fps") -> return Command.SetFramerate(30)
            t.contains("live glass") || t.contains("smooth fps") -> return Command.SetFramerate(60)
        }
        Regex("(?:zoom rate|mandel rate|julia rate|set zoom)\\s*(\\d+(?:\\.\\d+)?)").find(t)?.let { m ->
            return Command.FractalZoomRate(m.groupValues[1].toFloat().coerceIn(0.15f, 6f))
        }
        when {
            t.contains("fractal key chroma") || t.contains("key fractal chroma") ||
                t.contains("chromakey fractal") || t.contains("chroma key fractal") ->
                return Command.FractalKeyMode(3)
            t.contains("fractal key bright") || t.contains("fractal in brights") ->
                return Command.FractalKeyMode(2)
            t.contains("fractal key dark") || t.contains("fractal in dark") ||
                t.contains("fractal in shadows") ->
                return Command.FractalKeyMode(1)
            t.contains("fractal full") || t.contains("fractal mix full") ->
                return Command.FractalKeyMode(0)
        }

        // Switchboard vocabulary (UI twins)
        parseBoard(t)?.let { return it }

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

    private fun parseBoard(t: String): Command? {
        if (t.contains("clear board")) return Command.BoardClear
        if (t.contains("save preset")) {
            val name = t.substringAfter("save preset").trim().ifEmpty { "voice" }
            return Command.BoardSavePreset(name.replace(" ", "_"))
        }
        if (t.contains("load preset")) {
            val name = t.substringAfter("load preset").trim().ifEmpty { return null }
            return Command.BoardLoadPreset(name.replace(" ", "_"))
        }
        // add effect time trails 2
        Regex("(?:add effect time|fade in)\\s+(\\w+)\\s+(\\d+(?:\\.\\d+)?)").find(t)?.let { m ->
            return Command.BoardFadeIn(m.groupValues[1], m.groupValues[2].toFloat())
        }
        Regex("(?:remove effect time|fade out)\\s+(\\w+)\\s+(\\d+(?:\\.\\d+)?)").find(t)?.let { m ->
            return Command.BoardFadeOut(m.groupValues[1], m.groupValues[2].toFloat())
        }
        Regex("phase time\\s+(\\w+)\\s+(\\d+(?:\\.\\d+)?)").find(t)?.let { m ->
            return Command.BoardPhaseTime(m.groupValues[1], m.groupValues[2].toFloat())
        }
        Regex("phase on\\s+(\\w+)").find(t)?.let { m ->
            return Command.BoardPhase(m.groupValues[1], true)
        }
        Regex("phase off\\s+(\\w+)").find(t)?.let { m ->
            return Command.BoardPhase(m.groupValues[1], false)
        }
        Regex("(?:add effect|add)\\s+(\\w+)").find(t)?.let { m ->
            val id = m.groupValues[1]
            if (id == "effect") return null
            return Command.BoardAdd(id)
        }
        Regex("(?:remove effect|remove)\\s+(\\w+)").find(t)?.let { m ->
            return Command.BoardRemove(m.groupValues[1])
        }
        Regex("toggle(?: effect)?\\s+(\\w+)").find(t)?.let { m ->
            return Command.BoardToggle(m.groupValues[1])
        }
        return null
    }
}
