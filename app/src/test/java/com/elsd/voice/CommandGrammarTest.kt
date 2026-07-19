package com.elsd.voice

import com.elsd.bounce.BounceMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommandGrammarTest {
    @Test
    fun clearWins() {
        assertEquals(Command.SafetyClear, CommandGrammar.parse("please clear now"))
        assertEquals(Command.SafetyClear, CommandGrammar.parse("sober"))
    }

    @Test
    fun toasted() {
        assertEquals(Command.Toasted, CommandGrammar.parse("toasted"))
    }

    @Test
    fun bounceMute() {
        val c = CommandGrammar.parse("mute bounce")
        assertTrue(c is Command.BounceModeSet)
        assertEquals(BounceMode.MUTED, (c as Command.BounceModeSet).mode)
    }

    @Test
    fun goghWalkPreset() {
        assertEquals(Command.Preset("gogh_walk"), CommandGrammar.parse("gogh walk"))
    }

    @Test
    fun paintGogh() {
        assertEquals(Command.Paint("gogh"), CommandGrammar.parse("gogh"))
    }
}
