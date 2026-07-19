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
    /** Mass toggle for Amy roll/bounce/teleport catalog. */
    var amyActionsEnabled: Boolean = true
    var presetName: String = "untitled"
    /** 0 = unlock */
    var targetFps: Int = 30
    var allowDroppedFrames: Boolean = true
    /**
     * Where fractal sits in the FOV:
     * 0 = full wet mix, 1 = luma key darks, 2 = luma key brights, 3 = chroma key
     */
    var fractalKeyMode: Int = 3
    var fractalKeyHue: Float = 0.33f
    /** When true, ADD CINEMA will not change targetFps (user chose UNLOCK). */
    var framerateHardLocked: Boolean = false

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
            if (id.family == EffectFamily.FILM) {
                CinemaCadence.applySoft(this, id, framerateHardLocked)
            }
            return existing
        }
        val layer = EffectLayer(id = id, enabled = true, clockSec = 0f)
        layers[id] = layer
        // Film styles own their cadence — variable FPS is part of the look
        if (id.family == EffectFamily.FILM) {
            CinemaCadence.applySoft(this, id, framerateHardLocked)
        }
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
        put("amyActionsEnabled", amyActionsEnabled)
        put("targetFps", targetFps)
        put("allowDroppedFrames", allowDroppedFrames)
        put("fractalKeyMode", fractalKeyMode)
        put("fractalKeyHue", fractalKeyHue.toDouble())
        put("framerateHardLocked", framerateHardLocked)
        val arr = JSONArray()
        layers.values.forEach { arr.put(it.toJson()) }
        put("layers", arr)
    }

    fun loadJson(o: JSONObject) {
        layers.clear()
        presetName = o.optString("presetName", "loaded")
        globalWet = o.optDouble("globalWet", 0.7).toFloat()
        amyMuted = o.optBoolean("amyMuted", false)
        amyActionsEnabled = o.optBoolean("amyActionsEnabled", true)
        targetFps = o.optInt("targetFps", 30)
        allowDroppedFrames = o.optBoolean("allowDroppedFrames", true)
        fractalKeyMode = o.optInt("fractalKeyMode", 3)
        fractalKeyHue = o.optDouble("fractalKeyHue", 0.33).toFloat()
        framerateHardLocked = o.optBoolean("framerateHardLocked", false)
        val arr = o.optJSONArray("layers") ?: return
        for (i in 0 until arr.length()) {
            val layer = EffectLayer.fromJson(arr.getJSONObject(i)) ?: continue
            layers[layer.id] = layer
        }
    }

    fun applyToMix(mix: com.elsd.mix.MixState) {
        mix.wet = globalWet
        mix.presetName = presetName
        mix.targetFps = targetFps
        mix.allowDroppedFrames = allowDroppedFrames
        mix.bounceMode = if (amyMuted) BounceMode.MUTED else BounceMode.FLAT

        mix.paintId = "none"
        mix.lsdId = "none"
        mix.deskId = "none"
        mix.cinemaId = "none"
        mix.paletteId = "none"
        mix.moodId = "mood_neutral"
        mix.stillnessMorph = false
        mix.trailOn = false
        mix.fractalMode = 0
        mix.fractalZoomRate = 1f
        mix.fractalKeyMode = fractalKeyMode
        mix.chromaHue = fractalKeyHue
        mix.keyMode = com.elsd.mix.KeyMode.OFF
        mix.pulseEnabled = false
        mix.spatialMode = com.elsd.audio.SpatialMode.OFF
        mix.earWet = 0f

        var last = "—"
        for (layer in layers.values) {
            val active = layer.enabled || layer.envelope() > 0.01f || layer.phaseEnabled
            if (!active) continue
            when (layer.id.family) {
                EffectFamily.DESK -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        when (layer.id) {
                            EffectId.TRAIL -> {
                                mix.trailOn = true
                                last = "DESK"
                            }
                            else -> {
                                // hue, split, negative, posterize, mirror
                                mix.deskId = layer.id.catalogName
                                last = "DESK"
                            }
                        }
                    }
                }
                EffectFamily.PSYCHEDELIC -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        when (layer.id) {
                            EffectId.STILLNESS_MORPH -> {
                                mix.stillnessMorph = true
                                last = "PSYCH"
                            }
                            EffectId.MANDELBROT -> {
                                mix.fractalMode = 1
                                mix.fractalZoomRate = layer.rate.coerceIn(0.15f, 6f)
                                last = "PSYCH"
                            }
                            EffectId.JULIA -> {
                                mix.fractalMode = 2
                                mix.fractalZoomRate = layer.rate.coerceIn(0.15f, 6f)
                                last = "PSYCH"
                            }
                            else -> {
                                // kaleido, melt
                                mix.lsdId = layer.id.catalogName
                                last = "PSYCH"
                            }
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
                EffectFamily.FILM -> {
                    if (layer.enabled || layer.envelope() > 0.01f) {
                        mix.cinemaId = layer.id.catalogName
                        last = "FILM"
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
                  