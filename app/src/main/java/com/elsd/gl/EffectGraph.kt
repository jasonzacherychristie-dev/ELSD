package com.elsd.gl

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.elsd.mix.KeyMode
import com.elsd.mix.MixState
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * TOASTED program graph:
 * WORLD (OES) → optional KEY with PLATE → PULSE → PAINT → LSD → PGM
 *
 * M1: WORLD passthrough + wet-blended LSD/paint stubs driven by uniforms.
 * Plate texture optional (0 = none).
 */
class EffectGraph(private val context: Context) {

    private var program: ShaderProgram? = null
    private var quad: FloatBuffer? = null
    private var time = 0f

    fun ensureGl() {
        if (program != null) return
        val vs = context.assets.open("shaders/fullscreen.vert").bufferedReader().readText()
        val fs = context.assets.open("shaders/toasted_composite.frag").bufferedReader().readText()
        program = ShaderProgram(vs, fs)
        val verts = floatArrayOf(
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f,
            -1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f,
        )
        quad = ByteBuffer.allocateDirect(verts.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(verts)
        quad!!.position(0)
    }

    fun draw(
        worldOesTex: Int,
        plateTex: Int,
        hasPlate: Boolean,
        stMatrix: FloatArray,
        mix: MixState,
        bass: Float,
        dt: Float,
    ) {
        ensureGl()
        val p = program ?: return
        val v = quad ?: return
        time += dt

        GLES30.glDisable(GLES30.GL_DEPTH_TEST)
        p.use()

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, worldOesTex)
        p.setUniform1i("uWorld", 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        if (hasPlate && plateTex != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, plateTex)
        } else {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        }
        p.setUniform1i("uPlate", 1)
        p.setUniform1i("uHasPlate", if (hasPlate) 1 else 0)

        // SurfaceTexture transform
        val mLoc = GLES30.glGetUniformLocation(p.id, "uStMatrix")
        GLES30.glUniformMatrix4fv(mLoc, 1, false, stMatrix, 0)

        p.setUniform1f("uTime", time)
        p.setUniform1f("uWet", mix.wet)
        p.setUniform1f("uBass", bass)
        p.setUniform1i(
            "uKeyMode",
            when (mix.keyMode) {
                KeyMode.OFF -> 0
                KeyMode.LUMA -> 1
                KeyMode.CHROMA -> 2
            },
        )
        p.setUniform1f("uChromaHue", mix.chromaHue)
        p.setUniform1i("uPulse", if (mix.pulseEnabled) 1 else 0)
        p.setUniform1i("uPaint", paintIndex(mix.paintId))
        p.setUniform1i("uLsd", lsdIndex(mix.lsdId))
        p.setUniform1i("uCinema", cinemaIndex(mix.cinemaId))
        p.setUniform1i("uPalette", paletteIndex(mix.paletteId))
        p.setUniform1i("uMood", moodIndex(mix.moodId))

        val aPos = p.attrib("aPos")
        val aUv = p.attrib("aUv")
        v.position(0)
        GLES30.glEnableVertexAttribArray(aPos)
        GLES30.glVertexAttribPointer(aPos, 2, GLES30.GL_FLOAT, false, 16, v)
        v.position(2)
        GLES30.glEnableVertexAttribArray(aUv)
        GLES30.glVertexAttribPointer(aUv, 2, GLES30.GL_FLOAT, false, 16, v)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
        GLES30.glDisableVertexAttribArray(aPos)
        GLES30.glDisableVertexAttribArray(aUv)
    }

    fun release() {
        program?.release()
        program = null
    }

    private fun paintIndex(id: String): Int = when (id) {
        "gogh" -> 1
        "monet" -> 2
        "noir" -> 3
        "neon" -> 4
        "sketch" -> 5
        "melt" -> 6
        "comic" -> 7
        "cartoon" -> 8
        "hyperreal" -> 9
        else -> 0
    }

    private fun lsdIndex(id: String): Int = when (id) {
        "trail" -> 1
        "hue" -> 2
        "split" -> 3
        "kaleido" -> 4
        "melt" -> 5
        "mandelbrot" -> 6
        "julia" -> 7
        else -> 0
    }

    private fun cinemaIndex(id: String): Int = when (id) {
        "noir" -> 1
        "neon" -> 2
        "bleach" -> 3
        "teal_orange" -> 4
        "anamorphic" -> 5
        "soft_glow" -> 6
        "technicolor" -> 7
        "suspiria" -> 8
        "silent_era" -> 9
        "expressionist" -> 10
        "giallo" -> 11
        "golden_age" -> 12
        else -> 0
    }

    private fun paletteIndex(id: String): Int = when (id) {
        "analog_film" -> 1
        "analog_vhs" -> 2
        "digital_clean" -> 3
        "digital_harsh" -> 4
        "polaroid" -> 5
        else -> 0
    }

    private fun moodIndex(id: String): Int = when (id) {
        "mood_calm" -> 1
        "mood_warm" -> 2
        "mood_cold" -> 3
        "mood_night" -> 4
        "mood_fever" -> 5
        "mood_toasted" -> 6
        else -> 0
    }
}
