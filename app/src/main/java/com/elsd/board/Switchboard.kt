package com.elsd.board

import com.elsd.audio.SpatialMode
import com.elsd.bounce.BounceMode
import org.json.JSONArray
import org.json.JSONObject

/**
 * Single switchboard model: ordered effect layers + global wet.
 * Source of truth for UI and for applying into live Mix/render.
 */
class Switchboard {
    private val layers = linkedMapOf<EffectId, EffectLayer>()
    var globalWet: Float = 0.7f
    var amyMuted: Boolean = false
    var presetName: String = "untitled"

    fun layersInOrder(): List<EffectLayer> = layers.values.toList()

    fun get(id: EffectId): EffectLayer? = layers[id]

    fun addEffect(id: EffectId): EffectLayer {
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

    /**
     * Project board into legacy MixState fields for current renderer.
     */
    fun applyToMix(mix: com.elsd.mix.MixState) {
        mix.wet = globalWet
        mix.presetName = presetName
        mix.bounceMode = if (amyMuted) BounceMode.MUTED else BounceMode.FLAT

        // Defaults off
        mix.keyMode = com.elsd.mix.KeyMode.OFF
        mix.pulseEnabled = false
        mix.paintId = "none"
        mix.lsdId = "none"
        mix.spatialMode = SpatialMode.OFF
        mix.earWet = 0f

        var last = "—"
        for (layer in layers.values) {
            if (layer.envelope() <= 0.001f && !layer.enabled) continue
            val on = layer.envelope() > 0.01f || layer.enabled
            if (!on && !layer.phaseEnabled) continue
            when (layer.id) {
                EffectId.KEY_LUMA -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.keyMode = com.elsd.mix.KeyMode.LUMA
                        last = "KEY"
                    }
                }
                EffectId.KEY_CHROMA -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.keyMode = com.elsd.mix.KeyMode.CHROMA
                        last = "KEY"
                    }
                }
                EffectId.CITY_PULSE -> {
                    mix.pulseEnabled = layer.enabled || layer.phaseEnabled
                    last = "PULSE"
                }
                EffectId.GOGH, EffectId.MONET, EffectId.NOIR, EffectId.NEON,
                EffectId.SKETCH, EffectId.MELT_PAINT,
                -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.paintId = when (layer.id) {
                            EffectId.MELT_PAINT -> "melt"
                            else -> layer.id.catalogName
                        }
                        last = "PAINT"
                    }
                }
                EffectId.TRAIL, EffectId.HUE, EffectId.SPLIT, EffectId.KALEIDO, EffectId.MELT -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.lsdId = layer.id.catalogName
                        last = "LSD"
                    }
                }
                EffectId.ORBIT, EffectId.BEHIND, EffectId.CATHEDRAL,
                EffectId.BASS_CRAWL, EffectId.HALLUCINATE_EARS,
                -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.spatialMode = SpatialMode.fromId(layer.id.catalogName)
                        mix.earWet = (layer.envelope() * globalWet).coerceIn(0f, 1f)
                        last = "EARS"
                    }
                }
            }
        }
        mix.lastBank = last
    }
}
