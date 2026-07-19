package com.elsd.bounce

import android.opengl.GLES30
import com.elsd.gl.ShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.min
import kotlin.math.sin

/**
 * **Amy** — spirited checker-orb pilot (demo-scene bounce energy, no restricted brand names).
 *
 * When the user starts speaking she **leans in**: larger, closer, face-light toward the camera,
 * a little attentive squash. When they stop, she eases back to idle float.
 */
class BounceCursor {

    private var program: ShaderProgram? = null
    private var quad: FloatBuffer? = null
    private var time = 0f

    /** Rest pose (NDC-ish center). */
    var homeX = 0f
    var homeY = 0.18f

    var mode: BounceMode = BounceMode.FLAT
    var listening = false
    var wet = 0.35f

    /** 0 = idle float, 1 = fully leaned in toward the user. Smoothed. */
    private var lean = 0f

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

        // Lean-in envelope: snappy approach, gentle settle back
        val target = if (listening) 1f else 0f
        val rate = if (listening) 7.5f else 3.2f
        lean += (target - lean) * min(1f, rate * dt)
        if (!listening && lean < 0.02f) lean = 0f

        // Idle bob; lean pulls Amy toward screen center / “closer” to the eyes
        val idleY = homeY + 0.028f * sin(time * 3.1f)
        val idleX = homeX + 0.012f * sin(time * 1.7f)
        val leanX = idleX * (1f - 0.72f * lean) // slide toward center
        val leanY = idleY * (1f - 0.55f * lean) + (-0.04f * lean) // slight dip = nodding in
        // Scale: lean-in grows her in FOV (closer to user)
        val baseSize = when (mode) {
            BounceMode.FLAT -> 0.12f
            BounceMode.SPATIAL -> 0.16f
            BounceMode.MUTED -> 0f
        }
        val size = baseSize * (1f + 0.55f * lean) *
            if (listening) (0.92f + 0.08f * sin(time * 14f)) else 1f

        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)

        p.use()
        p.setUniform1f("uTime", time)
        p.setUniform1f("uAspect", aspect)
        p.setUniform2f("uCenter", leanX, leanY)
        p.setUniform1f("uSize", size)
        p.setUniform1f("uWet", wet)
        p.setUniform1f("uLean", lean)
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
            uniform float uLean;
            out vec2 vUv;
            void main() {
                // Mild perspective squash: lean-in tips the disc toward the viewer
                float tip = 1.0 - 0.18 * uLean;
                vec2 p = aPos * uSize;
                p.y *= tip;
                p.x *= (1.0 + 0.08 * uLean); // wider face toward you
                p.x /= uAspect;
                // Subtle nod rotation in 2D
                float ang = -0.12 * uLean;
                float c = cos(ang), s = sin(ang);
                p = vec2(c * p.x - s * p.y, s * p.x + c * p.y);
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
            uniform float uLean;
            uniform int uSpatial;
            uniform int uListen;
            out vec4 oColor;
            void main() {
                float r = length(vUv);
                if (r > 1.0) discard;
                float cells = mix(6.0, 8.0, float(uSpatial));
                vec2 grid = floor((vUv + 1.0) * 0.5 * cells);
                float check = mod(grid.x + grid.y, 2.0);
                vec3 red = vec3(0.86, 0.12, 0.14);
                vec3 cream = vec3(0.96, 0.93, 0.86);
                vec3 col = mix(red, cream, check);

                // Sphere lighting — light swings forward when Amy leans in (face you)
                float z = sqrt(max(0.0, 1.0 - r * r));
                vec3 n = normalize(vec3(vUv, z));
                vec3 lightDir = normalize(mix(
                    vec3(0.35, 0.45, 0.75),
                    vec3(0.05, 0.15, 1.0),  // more frontal when leaning
                    uLean
                ));
                float light = clamp(dot(n, lightDir), 0.0, 1.0);
                float shade = mix(1.0, 0.42 + 0.58 * light, max(float(uSpatial), uLean * 0.85));
                col *= shade;

                // Specular “eye contact” glint when listening
                float spec = pow(max(0.0, light), 24.0) * uLean;
                col += vec3(1.0) * spec * 0.35;

                float rim = smoothstep(0.72, 1.0, r);
                if (uListen == 1 || uLean > 0.2) {
                    // Soft attentive lime, stronger as lean peaks
                    col = mix(col, vec3(0.55, 0.95, 0.35), rim * 0.55 * max(float(uListen), uLean));
                }
                col = mix(col, col * vec3(1.1, 0.9, 0.85), uWet * rim * 0.5);
                float alpha = smoothstep(1.0, 0.90, r);
                oColor = vec4(col, alpha);
            }
        """.trimIndent()
    }
}
