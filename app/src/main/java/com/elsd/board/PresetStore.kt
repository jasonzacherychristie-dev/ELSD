package com.elsd.board

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * PRESETS / USER SAVES
 * - factory/  shipped looks (re-seedable, not user-deletable from normal UI)
 * - user/     user saves (SAVE PREFS / SAVE PRESET)
 */
class PresetStore(context: Context) {
    private val root = File(context.filesDir, "presets").also { it.mkdirs() }
    private val factoryDir = File(root, "factory").also { it.mkdirs() }
    private val userDir = File(root, "user").also { it.mkdirs() }

    data class Entry(
        val name: String,
        val kind: Kind,
    ) {
        val label: String get() = when (kind) {
            Kind.FACTORY -> "FACTORY · ${name.uppercase()}"
            Kind.USER -> "USER · ${name.uppercase()}"
        }
    }

    enum class Kind { FACTORY, USER }

    fun listFactory(): List<String> = namesIn(factoryDir)
    fun listUser(): List<String> = namesIn(userDir)

    fun listAll(): List<Entry> =
        listFactory().map { Entry(it, Kind.FACTORY) } +
            listUser().map { Entry(it, Kind.USER) }

    fun saveUser(name: String, board: Switchboard): String {
        val safe = sanitize(name)
        board.presetName = safe
        File(userDir, "$safe.json").writeText(board.toJson().toString(2))
        return safe
    }

    /** Prefer user save over factory when names collide. */
    fun load(name: String, board: Switchboard): Boolean {
        val safe = sanitize(name)
        val user = File(userDir, "$safe.json")
        val factory = File(factoryDir, "$safe.json")
        val f = when {
            user.exists() -> user
            factory.exists() -> factory
            else -> return false
        }
        board.loadJson(JSONObject(f.readText()))
        return true
    }

    fun deleteUser(name: String): Boolean {
        val f = File(userDir, "${sanitize(name)}.json")
        return f.exists() && f.delete()
    }

    fun exists(name: String): Boolean {
        val safe = sanitize(name)
        return File(userDir, "$safe.json").exists() || File(factoryDir, "$safe.json").exists()
    }

    fun listSummary(): String {
        val f = listFactory()
        val u = listUser()
        return buildString {
            append("FACTORY: ")
            append(if (f.isEmpty()) "(none)" else f.joinToString())
            append(" · USER: ")
            append(if (u.isEmpty()) "(none)" else u.joinToString())
        }
    }

    /** Always ensure factory seeds exist (does not wipe user/). Migrates legacy flat presets. */
    fun ensureFactory() {
        migrateLegacyFlatFiles()
        if (listFactory().isNotEmpty()) return
        seedAllFactory()
    }

    private fun migrateLegacyFlatFiles() {
        root.listFiles()?.forEach { f ->
            if (f.isFile && f.extension == "json") {
                val dest = File(userDir, f.name)
                if (!dest.exists()) f.copyTo(dest)
                f.delete()
            }
        }
    }

    private fun seedAllFactory() {
        fun seed(name: String, build: Switchboard.() -> Unit) {
            val b = Switchboard()
            b.presetName = name
            b.build()
            File(factoryDir, "${sanitize(name)}.json").writeText(b.toJson().toString(2))
        }
        seed("gogh_walk") {
            globalWet = 0.55f
            addEffect(EffectId.GOGH)
            addEffect(EffectId.TRAIL).fadeInSec = 1f
            addEffect(EffectId.MOOD_WARM)
        }
        seed("noir_night") {
            globalWet = 0.7f
            targetFps = 24
            allowDroppedFrames = true
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
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.SUSPIRIA)
            addEffect(EffectId.MOOD_FEVER)
            addEffect(EffectId.ANALOG_FILM)
        }
        seed("technicolor_dream") {
            globalWet = 0.7f
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.TECHNICOLOR)
            addEffect(EffectId.MOOD_WARM)
            addEffect(EffectId.SOFT_GLOW).fadeInSec = 1f
        }
        seed("silent_reel") {
            globalWet = 0.85f
            targetFps = 12
            allowDroppedFrames = true
            addEffect(EffectId.SILENT_ERA)
            addEffect(EffectId.MOOD_NEUTRAL)
        }
        seed("cabinet_shadows") {
            globalWet = 0.75f
            targetFps = 16
            allowDroppedFrames = true
            addEffect(EffectId.EXPRESSIONIST)
            addEffect(EffectId.MOOD_NIGHT)
        }
        seed("saturday_cel") {
            globalWet = 0.8f
            addEffect(EffectId.CARTOON)
            addEffect(EffectId.MOOD_WARM)
            addEffect(EffectId.DIGITAL_CLEAN)
        }
        seed("uncanny_sharp") {
            globalWet = 0.7f
            addEffect(EffectId.HYPERREAL)
            addEffect(EffectId.MOOD_NEUTRAL)
            addEffect(EffectId.DIGITAL_HARSH)
        }
        seed("stare_dream") {
            globalWet = 0.65f
            targetFps = 15
            allowDroppedFrames = true
            addEffect(EffectId.STILLNESS_MORPH)
            addEffect(EffectId.MELT).fadeInSec = 2f
            addEffect(EffectId.SOFT_GLOW)
            addEffect(EffectId.MOOD_CALM)
        }
        seed("mandel_room") {
            globalWet = 0.75f
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.MANDELBROT)
            addEffect(EffectId.MOOD_TOASTED)
        }
        seed("julia_seed") {
            globalWet = 0.8f
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.JULIA)
            addEffect(EffectId.MOOD_FEVER)
            addEffect(EffectId.NEON).fadeInSec = 1f
        }
        seed("mandel_key_trails") {
            globalWet = 0.85f
            targetFps = 24
            allowDroppedFrames = true
            fractalKeyMode = 3
            fractalKeyHue = 0.33f
            addEffect(EffectId.MANDELBROT).apply {
                rate = 1.25f
                fadeInSec = 1.5f
            }
            addEffect(EffectId.TRAIL).apply {
                fadeInSec = 0.5f
                wet = 0.9f
            }
            addEffect(EffectId.MOOD_TOASTED)
        }
        seed("mandel_shadow_dive") {
            globalWet = 0.8f
            targetFps = 20
            allowDroppedFrames = true
            fractalKeyMode = 1
            addEffect(EffectId.MANDELBROT).rate = 2.0f
            addEffect(EffectId.TRAIL)
            addEffect(EffectId.MOOD_NIGHT)
        }
        seed("looking_glass") {
            globalWet = 0.85f
            targetFps = 30
            allowDroppedFrames = true
            addEffect(EffectId.MIRROR_QUAD)
            addEffect(EffectId.MOOD_COLD)
            addEffect(EffectId.DIGITAL_CLEAN)
        }
        seed("crystal_spin") {
            globalWet = 0.8f
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.KALEIDO_SPIN)
            addEffect(EffectId.TRAIL).wet = 0.7f
            addEffect(EffectId.MOOD_FEVER)
        }
        seed("diamond_room") {
            globalWet = 0.75f
            targetFps = 24
            allowDroppedFrames = true
            addEffect(EffectId.KALEIDO_8)
            addEffect(EffectId.MIRROR_V)
            addEffect(EffectId.MOOD_TOASTED)
        }
    }

    /** @deprecated use ensureFactory */
    fun seedFactoryIfEmpty() = ensureFactory()

    private fun namesIn(dir: File): List<String> =
        dir.listFiles()?.mapNotNull { f ->
            f.name.removeSuffix(".json").takeIf { f.isFile && f.extension == "json" }
        }?.sorted().orEmpty()

    private fun sanitize(name: String): String =
        name.trim().lowercase().replace(Regex("[^a-z0-9_\\-]+"), "_").ifEmpty { "preset" }
}
