package com.elsd.bounce

import android.opengl.GLES30
import com.elsd.gl.ShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.sin

/**
 * Bounce — spirited checker-orb cursor.
 * Implies classic demo-scene bounce energy without naming restricted brands.
 */
class BounceCursor {

    private var program: ShaderProgram? = null
    private var quad: FloatBuffer? = null
    private var time = 0f

    var mode: BounceMode = BounceMode.FLAT
    var x = 0f
    var y = 0.15f
    var listening = false
    var wet = 0.35f

    fun ensureGl() {
        if (program != null) return
        program = ShaderProgram(VERTEX, FRAGMENT)
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

    fun draw(aspect: Float, dt: Float) {
        if (mode == BounceMode.MUTED) return
        ensureGl()
        val p = program ?: return
        val v = quad ?: return
        time += dt

        // Soft idle float + squash when "listening"
        val bounceY = y + 0.03f * sin(time * 3.2f)
        val squash = if (listening) 0.85f + 0.15f * sin(time * 18f) else 1f
        val size = when (mode) {
            BounceMode.FLAT -> 0.12f
            BounceMode.SPATIAL -> 0.16f
            BounceMode.MUTED -> 0f
        } * squash

        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        p.use()
        p.setUniform1f("uTime", time)
        p.setUniform1f("uAspect", aspect)
        p.setUniform2f("uCenter", x, bounceY)
        p.setUniform1f("uSize", size)
        p.setUniform1f("uWet", wet)
        p.setUniform1i("uSpatial", if (mode == BounceMode.SPATIAL) 1 else 0)
        p.setUniform1i("uListen", if (listening) 1 else 0)

        val aPos = p.attrib("aPos")
        val aUv = p.attrib("aUv")
        v.position(0)
        GLES30.glVertexAttribPointer(aPos, 2, GLES30.GL_FLOAT, false, 16, v)
        GLES30.glEnableVertexAttribArray(aPos)
        v.position(2)
        GLES30.glVertexAttribPointer(aUv, 2, GLES30.GL_FLOAT, false, 16, v)
        GLES30.glEnableVertexAttribArray(aUv)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
        GLES30.glDisableVertexAttribArray(aPos)
        GLES30.glDisableVertexAttribArray(aUv)
        GLES30.glDisable(GLES30.GL_BLEND)
    }

    fun release() {
        program?.release()
        program = null
    }

    companion object {
        private val VERTEX = """
            #version 300 es
            layout(location = 0) in vec2 aPos;
            layout(location = 1) in vec2 aUv;
            uniform vec2 uCenter;
            uniform float uSize;
            uniform float uAspect;
            out vec2 vUv;
            void main() {
                vec2 p = aPos * uSize;
                p.x /= uAspect;
                gl_Position = vec4(p + uCenter, 0.0, 1.0);
                vUv = aUv * 2.0 - 1.0;
            }
        """.trimIndent()

        private val FRAGMENT = """
            #version 300 es
            precision mediump float;
            in vec2 vUv;
            uniform float uTime;
            uniform float uWet;
            uniform int uSpatial;
            uniform int uListen;
            out vec4 oColor;
            void main() {
                float r = length(vUv);
                if (r > 1.0) discard;
                // Checker orb — red / cream
                float cells = mix(6.0, 8.0, float(uSpatial));
                vec2 grid = floor((vUv + 1.0) * 0.5 * cells);
                float check = mod(grid.x + grid.y, 2.0);
                vec3 red = vec3(0.86, 0.12, 0.14);
                vec3 cream = vec3(0.96, 0.93, 0.86);
                vec3 col = mix(red, cream, check);
                // Soft sphere shading when spatial
                if (uSpatial == 1) {
                    float z = sqrt(max(0.0, 1.0 - r * r));
                    vec3 n = normalize(vec3(vUv, z));
                    float light = clamp(dot(n, normalize(vec3(0.3, 0.5, 0.8))), 0.0, 1.0);
                    col *= 0.45 + 0.55 * light;
                }
                // Lime listen rim
                float rim = smoothstep(0.75, 1.0, r);
                if (uListen == 1) {
                    col = mix(col, vec3(0.55, 0.95, 0.35), rim * 0.85);
                }
                // Wet pride — warmer rim when toasted
                col = mix(col, col * vec3(1.1, 0.9, 0.85), uWet * rim * 0.5);
                float alpha = smoothstep(1.0, 0.92, r);
                oColor = vec4(col, alpha);
            }
        """.trimIndent()
    }
}
