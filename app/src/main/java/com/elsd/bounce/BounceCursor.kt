package com.elsd.bounce

import android.opengl.GLES30
import com.elsd.gl.ShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * **Amy** — red/white checker pilot on one cheap quad.
 *
 * Actions: lean, bounce, roll, teleport, hop, spin, ack, pop.
 * [actionsEnabled] toggles the whole motion catalog en masse (she can still sit still).
 */
class BounceCursor {

    private var program: ShaderProgram? = null
    private var quad: FloatBuffer? = null
    private var time = 0f

    var homeX = 0.35f
    var homeY = 0.22f

    var mode: BounceMode = BounceMode.FLAT
    var listening = false
    var wet = 0.35f

    /** Mass toggle for cute motion (roll/bounce/teleport/idle bob/lean flourish). */
    var actionsEnabled: Boolean = true

    private var lean = 0f
    private var action = AmyAction.IDLE
    private var actionT = 0f
    private var fromX = 0f
    private var fromY = 0f
    private var toX = 0f
    private var toY = 0f
    private var spin = 0f
    private var squash = 1f
    private var alphaMul = 1f
    private var posX = homeX
    private var posY = homeY

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
        posX = homeX
        posY = homeY
    }

    /** Play a one-shot (or replace current). No-op if actions mass-disabled (except lean via listening). */
    fun play(action: AmyAction, rng: Random = Random.Default) {
        if (!actionsEnabled && action != AmyAction.IDLE) return
        if (mode == BounceMode.MUTED) return
        this.action = action
        actionT = 0f
        fromX = posX
        fromY = posY
        when (action) {
            AmyAction.ROLL, AmyAction.TELEPORT, AmyAction.HOP -> {
                toX = rng.nextDouble(-0.55, 0.55).toFloat()
                toY = rng.nextDouble(0.05, 0.45).toFloat()
                homeX = toX
                homeY = toY
            }
            AmyAction.BOUNCE, AmyAction.SPIN, AmyAction.ACK, AmyAction.POP -> {
                toX = posX
                toY = posY
            }
            else -> {
                toX = posX
                toY = posY
            }
        }
    }

    /** Random cute antics (for “amy dance” / after command). */
    fun playRandomCute(rng: Random = Random.Default) {
        val pool = listOf(
            AmyAction.BOUNCE, AmyAction.ROLL, AmyAction.TELEPORT,
            AmyAction.HOP, AmyAction.SPIN, AmyAction.POP, AmyAction.ACK,
        )
        play(pool.random(rng), rng)
    }

    fun draw(aspect: Float, dt: Float) {
        if (mode == BounceMode.MUTED) return
        ensureGl()
        val p = program ?: return
        val v = quad ?: return
        time += dt

        // Lean always available when listening (even if antics off) — communication
        val leanTarget = if (listening) 1f else 0f
        val leanRate = if (listening) 7.5f else 3.2f
        lean += (leanTarget - lean) * min(1f, leanRate * dt)
        if (!listening && lean < 0.02f) lean = 0f

        // Advance one-shot action
        if (actionsEnabled && action != AmyAction.IDLE && action.durationSec > 0f) {
            actionT += dt
            val u = (actionT / action.durationSec).coerceIn(0f, 1f)
            applyActionPose(u)
            if (u >= 1f) {
                action = AmyAction.IDLE
                actionT = 0f
                squash = 1f
                alphaMul = 1f
                spin = 0f
                posX = homeX
                posY = homeY
            }
        } else if (actionsEnabled && action == AmyAction.IDLE) {
            // Idle float
            posX = homeX + 0.012f * sin(time * 1.7f)
            posY = homeY + 0.028f * sin(time * 3.1f)
            squash = 1f
            alphaMul = 1f
            spin *= 0.9f
        } else if (!actionsEnabled) {
            // Mass off: park still at home (still may lean for speech)
            posX = homeX
            posY = homeY
            squash = 1f
            alphaMul = 1f
            spin = 0f
            if (action != AmyAction.IDLE && action != AmyAction.LEAN_IN && action != AmyAction.LEAN_HOLD) {
                action = AmyAction.IDLE
            }
        }

        val leanX = posX * (1f - 0.72f * lean)
        val leanY = posY * (1f - 0.55f * lean) + (-0.04f * lean)
        val baseSize = when (mode) {
            BounceMode.FLAT -> 0.12f
            BounceMode.SPATIAL -> 0.16f
            BounceMode.MUTED -> 0f
        }
        val size = baseSize * (1f + 0.55f * lean) * squash *
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
        p.setUniform1f("uSpin", spin)
        p.setUniform1f("uAlphaMul", alphaMul)
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

    private fun applyActionPose(u: Float) {
        val e = smooth(u)
        when (action) {
            AmyAction.BOUNCE -> {
                // Arc hop in place
                val hop = sin(u * Math.PI.toFloat())
                posX = fromX
                posY = fromY + 0.18f * hop
                squash = 1f - 0.25f * sin(u * Math.PI.toFloat() * 2f)
            }
            AmyAction.ROLL -> {
                posX = fromX + (toX - fromX) * e
                posY = fromY + (toY - fromY) * e + 0.06f * sin(u * Math.PI.toFloat())
                spin = u * 6.28318f * 2f
                squash = 1f - 0.1f * sin(u * Math.PI.toFloat())
            }
            AmyAction.TELEPORT -> {
                if (u < 0.45f) {
                    val t = u / 0.45f
                    alphaMul = 1f - t
                    squash = 1f + 0.4f * t
                    posX = fromX
                    posY = fromY
                } else {
                    val t = (u - 0.45f) / 0.55f
                    alphaMul = t
                    squash = 1.4f - 0.4f * t
                    posX = toX
                    posY = toY
                    spin = (1f - t) * 3f
                }
            }
            AmyAction.HOP -> {
                posX = fromX + (toX - fromX) * e
                posY = fromY + (toY - fromY) * e + 0.12f * sin(u * Math.PI.toFloat())
                squash = 1f - 0.2f * sin(u * Math.PI.toFloat())
            }
            AmyAction.SPIN -> {
                posX = fromX
                posY = fromY
                spin = u * 6.28318f * 3f
                squash = 1f + 0.15f * sin(u * Math.PI.toFloat())
            }
            AmyAction.ACK -> {
                posX = fromX
                posY = fromY
                squash = 1f - 0.35f * sin(u * Math.PI.toFloat())
            }
            AmyAction.POP -> {
                posX = fromX
                posY = fromY + 0.08f * sin(u * Math.PI.toFloat())
                squash = 1f + 0.35f * sin(u * Math.PI.toFloat())
                alphaMul = 0.85f + 0.15f * sin(u * Math.PI.toFloat())
            }
            else -> { }
        }
    }

    fun release() {
        program?.release()
        program = null
    }

    private fun smooth(x: Float): Float = x * x * (3f - 2f * x)

    companion object {
        private val VERTEX = """
            #version 300 es
            layout(location = 0) in vec2 aPos;
            layout(location = 1) in vec2 aUv;
            uniform vec2 uCenter;
            uniform float uSize;
            uniform float uAspect;
            uniform float uLean;
            uniform float uSpin;
            out vec2 vUv;
            void main() {
                float tip = 1.0 - 0.18 * uLean;
                vec2 p = aPos * uSize;
                p.y *= tip;
                p.x *= (1.0 + 0.08 * uLean);
                // roll / spin
                float c = cos(uSpin), s = sin(uSpin);
                p = vec2(c * p.x - s * p.y, s * p.x + c * p.y);
                p.x /= uAspect;
                float ang = -0.12 * uLean;
                c = cos(ang); s = sin(ang);
                p = vec2(c * p.x - s * p.y, s * p.x + c * p.y);
                gl_Position = vec4(p + uCenter, 0.0, 1.0);
                // spin UVs so checks tumble when rolling
                vec2 uv = aUv * 2.0 - 1.0;
                float c2 = cos(-uSpin), s2 = sin(-uSpin);
                uv = vec2(c2 * uv.x - s2 * uv.y, s2 * uv.x + c2 * uv.y);
                vUv = uv;
            }
        """.trimIndent()

        private val FRAGMENT = """
            #version 300 es
            precision mediump float;
            in vec2 vUv;
            uniform float uTime;
            uniform float uWet;
            uniform float uLean;
            uniform float uAlphaMul;
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

                float z = sqrt(max(0.0, 1.0 - r * r));
                vec3 n = normalize(vec3(vUv, z));
                vec3 lightDir = normalize(mix(
                    vec3(0.35, 0.45, 0.75),
                    vec3(0.05, 0.15, 1.0),
                    uLean
                ));
                float light = clamp(dot(n, lightDir), 0.0, 1.0);
                float shade = mix(1.0, 0.42 + 0.58 * light, max(float(uSpatial), uLean * 0.85));
                col *= shade;

                float spec = pow(max(0.0, light), 24.0) * uLean;
                col += vec3(1.0) * spec * 0.35;

                float rim = smoothstep(0.72, 1.0, r);
                if (uListen == 1 || uLean > 0.2) {
                    col = mix(col, vec3(0.55, 0.95, 0.35), rim * 0.55 * max(float(uListen), uLean));
                }
                col = mix(col, col * vec3(1.1, 0.9, 0.85), uWet * rim * 0.5);
                float alpha = smoothstep(1.0, 0.90, r) * uAlphaMul;
                oColor = vec4(col, alpha);
            }
        """.trimIndent()
    }
}
