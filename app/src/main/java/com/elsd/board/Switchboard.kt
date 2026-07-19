package com.elsd.board

import com.elsd.bounce.BounceMode
import org.json.JSONArray
import org.json.JSONObject

/**
 * Visual switchboard: ELSD · ART · CINEMA · PALETTE · MOOD.
 * PALETTE and MOOD are single-select (last add wins within family).
 */
class Switchboard {
    private val layers = linkedMapOf<EffectId, EffectLayer>()
    var globalWet: Float = 0.7f
    var amyMuted: Boolean = false
    var presetName: String = "untitled"

    fun layersInOrder(): List<EffectLayer> = layers.values.toList()

    fun get(id: EffectId): EffectLayer? = layers[id]

    fun addEffect(id: EffectId): EffectLayer {
        // Single-select families
        if (id.family == EffectFamily.MOOD || id.family == EffectFamily.PALETTE) {
            layers.keys.filter { layers[it]?.id?.family == id.family && it != id }
                .toList()
                .forEach { layers.remove(it) }
        }
        val existing = layers[id]
        if (existing != null) {
            existing.onEnabledChanged(true)
            return existing
        }
        val layer = EffectLayer(id = id, enabled = true, clockSec = 0f)
        layers[id] = layer
        return layer
    }

    fun removeEffect(id: EffectId) {
        layers.remove(id)
    }

    fun toggleEffect(id: EffectId) {
        val layer = layers[id] ?: run {
            addEffect(id)
            return
        }
        layer.onEnabledChanged(!layer.enabled)
    }

    fun setFadeIn(id: EffectId, sec: Float) {
        val layer = layers[id] ?: addEffect(id)
        layer.fadeInSec = sec.coerceAtLeast(0f)
    }

    fun setFadeOut(id: EffectId, sec: Float) {
        val layer = layers[id] ?: addEffect(id)
        layer.fadeOutSec = sec.coerceAtLeast(0f)
    }

    fun setPhaseTime(id: EffectId, sec: Float) {
        val layer = layers[id] ?: addEffect(id)
        layer.phaseSec = sec.coerceAtLeast(0f)
    }

    fun setPhaseEnabled(id: EffectId, on: Boolean) {
        val layer = layers[id] ?: addEffect(id)
        layer.phaseEnabled = on
        if (on) layer.clockSec = 0f
    }

    fun clearBoard() {
        layers.clear()
        presetName = "clear"
    }

    fun tick(dt: Float) {
        layers.values.forEach { it.tick(dt) }
    }

    fun envelope(id: EffectId): Float = layers[id]?.envelope() ?: 0f

    fun activeMood(): EffectId? =
        layers.values.firstOrNull {
            it.id.family == EffectFamily.MOOD && (it.enabled || it.envelope() > 0.01f)
        }?.id

    fun activePalette(): EffectId? =
        layers.values.firstOrNull {
            it.id.family == EffectFamily.PALETTE && (it.enabled || it.envelope() > 0.01f)
        }?.id

    fun toJson(): JSONObject = JSONObject().apply {
        put("presetName", presetName)
        put("globalWet", globalWet.toDouble())
        put("amyMuted", amyMuted)
        val arr = JSONArray()
        layers.values.forEach { arr.put(it.toJson()) }
        put("layers", arr)
    }

    fun loadJson(o: JSONObject) {
        layers.clear()
        presetName = o.optString("presetName", "loaded")
        globalWet = o.optDouble("globalWet", 0.7).toFloat()
        amyMuted = o.optBoolean("amyMuted", false)
        val arr = o.optJSONArray("layers") ?: return
        for (i in 0 until arr.length()) {
            val layer = EffectLayer.fromJson(arr.getJSONObject(i)) ?: continue
            layers[layer.id] = layer
        }
    }

    fun applyToMix(mix: com.elsd.mix.MixState) {
        mix.wet = globalWet
        mix.presetName = presetName
        mix.bounceMode = if (amyMuted) BounceMode.MUTED else BounceMode.FLAT

        mix.paintId = "none"
        mix.lsdId = "none"
        mix.cinemaId = "none"
        mix.paletteId = "none"
        mix.moodId = "mood_neutral"
        mix.stillnessMorph = false
        mix.keyMode = com.elsd.mix.KeyMode.OFF
        mix.pulseEnabled = false
        mix.spatialMode = com.elsd.audio.SpatialMode.OFF
        mix.earWet = 0f

        var last = "—"
        for (layer in layers.values) {
            val active = layer.enabled || layer.envelope() > 0.01f || layer.phaseEnabled
            if (!active) continue
            when (layer.id.family) {
                EffectFamily.ELSD -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        if (layer.id == EffectId.STILLNESS_MORPH) {
                            mix.stillnessMorph = true
                            last = "ELSD"
                        } else {
                            mix.lsdId = layer.id.catalogName
                            last = "ELSD"
                        }
                    }
                }
                EffectFamily.ART -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.paintId = when (layer.id) {
                            EffectId.MELT_PAINT -> "melt"
                            else -> layer.id.catalogName
                        }
                        last = "ART"
                    }
                }
                EffectFamily.CINEMA -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.cinemaId = layer.id.catalogName
                        last = "CINEMA"
                    }
                }
                EffectFamily.PALETTE -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.paletteId = layer.id.catalogName
                        last = "PALETTE"
                    }
                }
                EffectFamily.MOOD -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.moodId = layer.id.catalogName
                        last = "MOOD"
                    }
                }
            }
        }
        mix.lastBank = last
    }
}
