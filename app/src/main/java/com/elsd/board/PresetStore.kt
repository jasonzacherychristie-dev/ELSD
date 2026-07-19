package com.elsd.board

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * SAVE PRESET / LOAD PRESET — visual boards only for 1.0 factory seeds.
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
        seed("gogh_walk") {
            globalWet = 0.55f
            addEffect(EffectId.GOGH)
            addEffect(EffectId.TRAIL).fadeInSec = 1f
            addEffect(EffectId.MOOD_WARM)
        }
        seed("noir_night") {
            globalWet = 0.7f
            addEffect(EffectId.NOIR)
            addEffect(EffectId.DIGITAL_HARSH)
            addEffect(EffectId.MOOD_NIGHT)
        }
        seed("vhs_fever") {
            globalWet = 0.65f
            addEffect(EffectId.ANALOG_VHS)
            addEffect(EffectId.HUE).apply {
                phaseSec = 12f
                phaseEnabled = true
            }
            addEffect(EffectId.MOOD_FEVER)
        }
        seed("clean_calm") {
            globalWet = 0.4f
            addEffect(EffectId.DIGITAL_CLEAN)
            addEffect(EffectId.MOOD_CALM).fadeInSec = 2f
        }
        seed("toasted_desk") {
            globalWet = 0.75f
            addEffect(EffectId.MOOD_TOASTED)
            addEffect(EffectId.TRAIL).fadeInSec = 0.5f
            addEffect(EffectId.SOFT_GLOW)
        }
        seed("suspiria_red") {
            globalWet = 0.8f
            addEffect(EffectId.SUSPIRIA)
            addEffect(EffectId.MOOD_FEVER)
            addEffect(EffectId.ANALOG_FILM)
        }
        seed("technicolor_dream") {
            globalWet = 0.7f
            addEffect(EffectId.TECHNICOLOR)
            addEffect(EffectId.MOOD_WARM)
            addEffect(EffectId.SOFT_GLOW).fadeInSec = 1f
        }
        seed("silent_reel") {
            globalWet = 0.85f
            addEffect(EffectId.SILENT_ERA)
            addEffect(EffectId.MOOD_NEUTRAL)
        }
        seed("cabinet_shadows") {
            globalWet = 0.75f
            addEffect(EffectId.EXPRESSIONIST)
            addEffect(EffectId.MOOD_NIGHT)
        }
    }

    private fun sanitize(name: String): String =
        name.trim().lowercase().replace(Regex("[^a-z0-9_\\-]+"), "_").ifEmpty { "preset" }
}
