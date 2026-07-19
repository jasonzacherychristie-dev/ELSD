package com.elsd.board

import org.json.JSONObject

/**
 * One row on the switchboard. Envelope drives phase-in/out.
 */
data class EffectLayer(
    val id: EffectId,
    var enabled: Boolean = true,
    var fadeInSec: Float = 0.5f,
    var fadeOutSec: Float = 0.5f,
    /** Full cycle seconds when [phaseEnabled]; 0 = static while on. */
    var phaseSec: Float = 0f,
    var phaseEnabled: Boolean = false,
    var wet: Float = 1f,
    /** Elapsed since last enable edge or phase epoch. */
    var clockSec: Float = 0f,
) {
    fun tick(dt: Float) {
        if (enabled || phaseEnabled) {
            clockSec += dt
        }
    }

    /**
     * 0..1 contribution this frame from fade + optional phase.
     */
    fun envelope(): Float {
        if (!enabled && !phaseEnabled) return 0f
        if (!enabled && phaseEnabled) {
            // phasing while "armed" — still run cycle
        }
        if (!enabled && !phaseEnabled) return 0f

        if (phaseEnabled && phaseSec > 0.05f) {
            val t = (clockSec % phaseSec) / phaseSec // 0..1
            // Smooth triangle: in, out
            val tri = if (t < 0.5f) t * 2f else (1f - t) * 2f
            val base = if (enabled) tri else 0f
            return base * wet
        }

        if (!enabled) return 0f

        // One-shot fade in after enable (clock starts at 0 on enable)
        val fi = fadeInSec.coerceAtLeast(0.01f)
        val fade = (clockSec / fi).coerceIn(0f, 1f)
        return smooth(fade) * wet
    }

    fun onEnabledChanged(nowOn: Boolean) {
        enabled = nowOn
        clockSec = 0f
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id.catalogName)
        put("enabled", enabled)
        put("fadeInSec", fadeInSec.toDouble())
        put("fadeOutSec", fadeOutSec.toDouble())
        put("phaseSec", phaseSec.toDouble())
        put("phaseEnabled", phaseEnabled)
        put("wet", wet.toDouble())
    }

    companion object {
        fun fromJson(o: JSONObject): EffectLayer? {
            val id = EffectId.fromCatalog(o.getString("id")) ?: return null
            return EffectLayer(
                id = id,
                enabled = o.optBoolean("enabled", true),
                fadeInSec = o.optDouble("fadeInSec", 0.5).toFloat(),
                fadeOutSec = o.optDouble("fadeOutSec", 0.5).toFloat(),
                phaseSec = o.optDouble("phaseSec", 0.0).toFloat(),
                phaseEnabled = o.optBoolean("phaseEnabled", false),
                wet = o.optDouble("wet", 1.0).toFloat(),
            )
        }

        private fun smooth(x: Float): Float = x * x * (3f - 2f * x)
    }
}
