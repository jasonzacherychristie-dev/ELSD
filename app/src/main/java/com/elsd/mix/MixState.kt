package com.elsd.mix

import com.elsd.audio.SpatialMode
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
    @Volatile var chromaHue: Float = 0.33f // default green-ish
    @Volatile var pulseEnabled: Boolean = false
    @Volatile var paintId: String = "none"
    @Volatile var lsdId: String = "none"
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
            is Command.Unknown -> { /* ignore */ }
        }
    }

    private fun softClear() {
        wet = 0f
        lsdId = "none"
        // Keep paint/pulse gentle; clear means sober eyes + ears
        pulseEnabled = false
        paintId = "none"
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
        presetName = name
        when (name) {
            "window_tv" -> {
                keyMode = KeyMode.LUMA
                wet = 0.7f
                lsdId = "trail"
                paintId = "none"
                lastBank = "KEY"
            }
            "gogh_walk" -> {
                paintId = "gogh"
                pulseEnabled = true
                lsdId = "trail"
                wet = 0.55f
                lastBank = "PAINT"
            }
            "sky_cinema" -> {
                keyMode = KeyMode.CHROMA
                chromaHue = 0.58f
                wet = 0.75f
                lastBank = "KEY"
            }
            "quiet_toasted" -> {
                wet = 0.8f
                lsdId = "melt"
                bounceMode = BounceMode.MUTED
                spatialMode = SpatialMode.ORBIT
                earWet = 0.65f
                lastBank = "LSD"
            }
            "full_hallucination" -> {
                wet = 0.75f
                pulseEnabled = true
                lsdId = "trail"
                spatialMode = SpatialMode.HALLUCINATE_EARS
                earWet = 0.8f
                lastBank = "EARS"
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
        return "PGM eyes=${"%.2f".format(wet)} ears=${spatialMode.id}/${"%.2f".format(earWet)}  " +
            "$bounce  bank=$lastBank  key=$keyMode  paint=$paintId  lsd=$lsdId  preset=$presetName"
    }
}

enum class KeyMode { OFF, LUMA, CHROMA }
