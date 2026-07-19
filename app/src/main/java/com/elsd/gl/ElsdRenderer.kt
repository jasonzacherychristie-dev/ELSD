package com.elsd.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.elsd.bounce.BounceCursor
import com.elsd.mix.MixState
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ElsdRenderer(
    private val context: Context,
    private val mix: MixState,
    private val onSurfaceReady: () -> Unit,
) : GLSurfaceView.Renderer {

    private val bounce = BounceCursor()
    private lateinit var graph: EffectGraph

    private var worldTex = 0
    private var worldSt: SurfaceTexture? = null
    private val stMatrix = FloatArray(16)
    private var width = 1
    private var height = 1
    private var lastNs = 0L
    private var listening = false

    fun worldSurfaceTexture(): SurfaceTexture? = worldSt

    fun onMixChanged() {
        // uniforms read each frame from mix
    }

    fun setListening(v: Boolean) {
        listening = v
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.05f, 0.05f, 0.06f, 1f)
        graph = EffectGraph(context)
        graph.ensureGl()
        bounce.ensureGl()

        val tex = IntArray(1)
        GLES30.glGenTextures(1, tex, 0)
        worldTex = tex[0]
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, worldTex)
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_LINEAR,
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_LINEAR,
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_CLAMP_TO_EDGE,
        )
        GLES30.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_CLAMP_TO_EDGE,
        )

        worldSt = SurfaceTexture(worldTex)
        worldSt?.setOnFrameAvailableListener {
            // GLSurfaceView continuous mode will pick up
        }
        // Identity-ish; SurfaceTexture updates real matrix
        android.opengl.Matrix.setIdentityM(stMatrix, 0)
        onSurfaceReady()
    }

    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        width = w.coerceAtLeast(1)
        height = h.coerceAtLeast(1)
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.nanoTime()
        val dt = if (lastNs == 0L) 0.016f else ((now - lastNs) / 1_000_000_000f).coerceIn(0f, 0.1f)
        lastNs = now

        try {
            worldSt?.updateTexImage()
            worldSt?.getTransformMatrix(stMatrix)
        } catch (_: Exception) {
            // first frames may throw before frames arrive
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        graph.draw(
            worldOesTex = worldTex,
            plateTex = 0,
            hasPlate = false,
            stMatrix = stMatrix,
            mix = mix,
            bass = 0f,
            dt = dt,
        )

        bounce.mode = mix.bounceMode
        bounce.wet = mix.wet
        bounce.listening = listening
        val aspect = width.toFloat() / height.toFloat()
        bounce.draw(aspect, dt)
    }
}
