package com.elsd.mix

import com.elsd.audio.SpatialMode
import com.elsd.board.BoardSession
import com.elsd.board.EffectId
import com.elsd.bounce.BounceMode
import com.elsd.voice.Command

/**
 * Live desk state — WORLD / PLATE / PGM mental model, wet/dry, banks.
 * Eyes (visual wet) and ears (spatial mode) can couple or diverge.
 * TOASTED: high wet with pride.
 */
class MixState {
    @Volatile var wet: Float = 0.35f
    @Volatile var keyMode: KeyMode = KeyMode.OFF
    @Volatile var chromaHue: Float = 0.33f
    @Volatile var pulseEnabled: Boolean = false
    /** ART family → shader paint path */
    @Volatile var paintId: String = "none"
    /** ELSD family */
    @Volatile var lsdId: String = "none"
    /** CINEMA family */
    @Volatile var cinemaId: String = "none"
    /** PALETTE family — analog/digital medium */
    @Volatile var paletteId: String = "none"
    /** MOOD global toggle */
    @Volatile var moodId: String = "mood_neutral"
    /** Stare-to-morph master (StillnessTracker + MorphBudget). */
    @Volatile var stillnessMorph: Boolean = false
    @Volatile var bounceMode: BounceMode = BounceMode.FLAT
    @Volatile var spatialMode: SpatialMode = SpatialMode.OFF
    @Volatile var earWet: Float = 0f
    @Volatile var jokes: Boolean = true
    @Volatile var lastBank: String = "—"
    @Volatile var presetName: String = "boot"

    fun applyCommand(cmd: Command) {
        when (cmd) {
            is Command.SafetyClear -> softClear()
            is Command.SafetyStop -> hardStop()
            is Command.SetWet -> wet = cmd.value.coerceIn(0f, 1f)
            is Command.Toasted -> wet = 0.85f
            is Command.KeyLuma -> {
                keyMode = KeyMode.LUMA
                lastBank = "KEY"
            }
            is Command.KeyChroma -> {
                keyMode = KeyMode.CHROMA
                lastBank = "KEY"
            }
            is Command.KeyOff -> keyMode = KeyMode.OFF
            is Command.PulseOn -> {
                pulseEnabled = true
                lastBank = "PULSE"
            }
            is Command.PulseOff -> pulseEnabled = false
            is Command.Paint -> {
                paintId = cmd.id
                lastBank = "PAINT"
            }
            is Command.Lsd -> {
                lsdId = cmd.id
                lastBank = "LSD"
            }
            is Command.BounceModeSet -> bounceMode = cmd.mode
            is Command.Jokes -> jokes = cmd.enabled
            is Command.Preset -> applyPreset(cmd.name)
            is Command.Spatial -> {
                spatialMode = SpatialMode.fromId(cmd.modeId)
                if (spatialMode != SpatialMode.OFF) {
                    earWet = earWet.coerceAtLeast(0.55f)
                    lastBank = "EARS"
                } else {
                    earWet = 0f
                }
            }
            is Command.EarWet -> earWet = cmd.value.coerceIn(0f, 1f)
            is Command.EarsDry -> {
                spatialMode = SpatialMode.OFF
                earWet = 0f
            }
            is Command.BoardAdd -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let { BoardSession.board.addEffect(it) }
                BoardSession.board.applyToMix(this)
            }
            is Command.BoardRemove -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let { BoardSession.board.removeEffect(it) }
                BoardSession.board.applyToMix(this)
            }
            is Command.BoardToggle -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let { BoardSession.board.toggleEffect(it) }
                BoardSession.board.applyToMix(this)
            }
            is Command.BoardFadeIn -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let {
                    BoardSession.board.setFadeIn(it, cmd.sec)
                }
            }
            is Command.BoardFadeOut -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let {
                    BoardSession.board.setFadeOut(it, cmd.sec)
                }
            }
            is Command.BoardPhaseTime -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let {
                    BoardSession.board.setPhaseTime(it, cmd.sec)
                }
            }
            is Command.BoardPhase -> {
                EffectId.fromCatalog(cmd.effectCatalog)?.let {
                    BoardSession.board.setPhaseEnabled(it, cmd.on)
                }
                BoardSession.board.applyToMix(this)
            }
            is Command.BoardSavePreset -> {
                if (BoardSession::presets.isInitialized) {
                    BoardSession.presets.save(cmd.name, BoardSession.board)
                }
            }
            is Command.BoardLoadPreset -> {
                if (BoardSession::presets.isInitialized) {
                    BoardSession.presets.load(cmd.name, BoardSession.board)
                    BoardSession.board.applyToMix(this)
                }
            }
            is Command.BoardClear -> {
                BoardSession.board.clearBoard()
                BoardSession.board.applyToMix(this)
            }
            is Command.Unknown -> { /* ignore */ }
        }
    }

    private fun softClear() {
        wet = 0f
        lsdId = "none"
        paintId = "none"
        cinemaId = "none"
        paletteId = "none"
        moodId = "mood_neutral"
        stillnessMorph = false
        pulseEnabled = false
        keyMode = KeyMode.OFF
        spatialMode = SpatialMode.OFF
        earWet = 0f
        presetName = "clear"
    }

    private fun hardStop() {
        softClear()
        bounceMode = BounceMode.MUTED
        presetName = "stop"
    }

    private fun applyPreset(name: String) {
        // Prefer switchboard JSON presets; legacy names map visually only
        if (BoardSession::presets.isInitialized &&
            BoardSession.presets.load(name, BoardSession.board)
        ) {
            BoardSession.board.applyToMix(this)
            return
        }
        presetName = name
        when (name) {
            "gogh_walk" -> {
                paintId = "gogh"
                lsdId = "trail"
                moodId = "mood_warm"
                wet = 0.55f
                lastBank = "ART"
            }
            "noir_night" -> {
                cinemaId = "noir"
                paintId = "noir"
                paletteId = "digital_harsh"
                moodId = "mood_night"
                wet = 0.7f
                lastBank = "CINEMA"
            }
            "vhs_fever" -> {
                paletteId = "analog_vhs"
                lsdId = "hue"
                moodId = "mood_fever"
                wet = 0.65f
                lastBank = "PALETTE"
            }
            "clean_calm" -> {
                paletteId = "digital_clean"
                moodId = "mood_calm"
                wet = 0.4f
                lastBank = "MOOD"
            }
            "toasted_desk" -> {
                moodId = "mood_toasted"
                lsdId = "trail"
                cinemaId = "soft_glow"
                wet = 0.75f
                lastBank = "MOOD"
            }
            else -> { /* keep state */ }
        }
    }

    fun statusLine(): String {
        val bounce = when (bounceMode) {
            BounceMode.FLAT -> "Amy FLAT"
            BounceMode.SPATIAL -> "Amy 3D"
            BounceMode.MUTED -> "Amy quiet"
        }
        return "PGM wet=${"%.2f".format(wet)} mood=$moodId pal=$paletteId " +
            "art=$paintId cine=$cinemaId elsd=$lsdId $bounce preset=$presetName"
    }
}

enum class KeyMode { OFF, LUMA, CHROMA }
