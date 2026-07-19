package com.elsd.voice

import com.elsd.bounce.BounceMode

sealed class Command {
    data object SafetyClear : Command()
    data object SafetyStop : Command()
    data class SetWet(val value: Float) : Command()
    data object Toasted : Command()
    data object KeyLuma : Command()
    data object KeyChroma : Command()
    data object KeyOff : Command()
    data object PulseOn : Command()
    data object PulseOff : Command()
    data class Paint(val id: String) : Command()
    data class Lsd(val id: String) : Command()
    data class BounceModeSet(val mode: BounceMode) : Command()
    data class Jokes(val enabled: Boolean) : Command()
    data class Preset(val name: String) : Command()
    /** Spatial audio hallucination — wet ears. */
    data class Spatial(val modeId: String) : Command()
    data class EarWet(val value: Float) : Command()
    data object EarsDry : Command()
    /** Switchboard vocabulary mirrored to voice. */
    data class BoardAdd(val effectCatalog: String) : Command()
    data class BoardRemove(val effectCatalog: String) : Command()
    data class BoardToggle(val effectCatalog: String) : Command()
    data class BoardFadeIn(val effectCatalog: String, val sec: Float) : Command()
    data class BoardFadeOut(val effectCatalog: String, val sec: Float) : Command()
    data class BoardPhaseTime(val effectCatalog: String, val sec: Float) : Command()
    data class BoardPhase(val effectCatalog: String, val on: Boolean) : Command()
    data class BoardSavePreset(val name: String) : Command()
    data class BoardLoadPreset(val name: String) : Command()
    data object BoardClear : Command()
    data class Unknown(val raw: String) : Command()
}
