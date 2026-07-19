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
    data class Unknown(val raw: String) : Command()
}
