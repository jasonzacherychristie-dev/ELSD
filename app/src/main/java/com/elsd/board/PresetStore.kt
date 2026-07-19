package com.elsd.board

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * SAVE PRESET / LOAD PRESET — JSON files on device.
 */
class PresetStore(context: Context) {
    private val dir = File(context.filesDir, "presets").also { it.mkdirs() }

    fun listNames(): List<String> =
        dir.listFiles()?.mapNotNull { f ->
            f.name.removeSuffix(".json").takeIf { f.extension == "json" }
        }?.sorted().orEmpty()

    fun save(name: String, board: Switchboard) {
        val safe = sanitize(name)
        board.presetName = safe
        File(dir, "$safe.json").writeText(board.toJson().toString(2))
    }

    fun load(name: String, board: Switchboard): Boolean {
        val f = File(dir, "${sanitize(name)}.json")
        if (!f.exists()) return false
        board.loadJson(JSONObject(f.readText()))
        return true
    }

    fun seedFactoryIfEmpty() {
        if (listNames().isNotEmpty()) return
        fun seed(name: String, build: Switchboard.() -> Unit) {
            val b = Switchboard()
            b.presetName = name
            b.build()
            save(name, b)
        }
        seed("window_tv") {
            globalWet = 0.7f
            addEffect(EffectId.KEY_LUMA)
            addEffect(EffectId.TRAIL).fadeInSec = 1f
        }
        seed("gogh_walk") {
            globalWet = 0.55f
            addEffect(EffectId.GOGH)
            addEffect(EffectId.CITY_PULSE)
            addEffect(EffectId.TRAIL)
        }
        seed("sky_cinema") {
            globalWet = 0.75f
            addEffect(EffectId.KEY_CHROMA)
        }
        seed("quiet_toasted") {
            globalWet = 0.8f
            amyMuted = true
            addEffect(EffectId.MELT).apply {
                fadeInSec = 2f
                phaseSec = 16f
                phaseEnabled = true
            }
            addEffect(EffectId.ORBIT).fadeInSec = 1.5f
        }
        seed("full_hallucination") {
            globalWet = 0.75f
            addEffect(EffectId.CITY_PULSE)
            addEffect(EffectId.TRAIL)
            addEffect(EffectId.HALLUCINATE_EARS)
        }
    }

    private fun sanitize(name: String): String =
        name.trim().lowercase().replace(Regex("[^a-z0-9_\\-]+"), "_").ifEmpty { "preset" }
}
