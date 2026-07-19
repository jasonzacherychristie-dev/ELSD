package com.elsd

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.elsd.board.BoardSession
import com.elsd.bounce.AmyAction
import com.elsd.bus.CameraBus
import com.elsd.gl.ElsdRenderer
import com.elsd.voice.Command
import com.elsd.voice.VoiceRouter
import com.elsd.mix.MixState

/**
 * Voice-first host. No touch mixer chrome — platform permission dialogs only.
 * Bounce + TOASTED legends are drawn in GL (and a minimal status strip for M1).
 */
class MainActivity : ComponentActivity() {

    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: ElsdRenderer
    private lateinit var cameraBus: CameraBus
    private lateinit var voiceRouter: VoiceRouter
    private lateinit var status: TextView

    private val mix = MixState()
    private var lastBoardTickNs = 0L

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        if (granted[Manifest.permission.CAMERA] == true) {
            startCamera()
        } else {
            status.text = getString(R.string.need_camera)
        }
        if (granted[Manifest.permission.RECORD_AUDIO] == true) {
            voiceRouter.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        val root = FrameLayout(this)
        glView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(3)
            renderer = ElsdRenderer(
                context = this@MainActivity,
                mix = mix,
                onSurfaceReady = {
                    runOnUiThread {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.CAMERA,
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            startCamera()
                        }
                    }
                },
            )
            setRenderer(renderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
        root.addView(
            glView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        status = TextView(this).apply {
            setTextColor(0xFFE8E0D0.toInt())
            textSize = 12f
            setPadding(24, 24, 24, 24)
            text = buildString {
                append(getString(R.string.boot_hint))
                append('\n')
                append(ElsdApp.CREDIT_SHORT)
            }
            // Status is informational only — not a control surface.
            importantForAccessibility = android.view.View.IMPORTANT_FOR_ACCESSIBILITY_YES
        }
        root.addView(
            status,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ),
        )
        setContentView(root)

        // Apply switchboard layers into live mix
        BoardSession.board.applyToMix(mix)

        cameraBus = CameraBus(this)
        voiceRouter = VoiceRouter(
            context = this,
            onCommand = { cmd ->
                runOnUiThread {
                    renderer.setListening(false)
                    when (cmd) {
                        is Command.AmyPlay -> {
                            AmyAction.fromId(cmd.actionId)?.let { renderer.playAmy(it) }
                        }
                        is Command.AmyAntics -> renderer.playAmyAntics()
                        else -> {
                            mix.applyCommand(cmd)
                            BoardSession.board.applyToMix(mix)
                            // cute ack after most commands
                            if (mix.amyActionsEnabled) {
                                renderer.playAmy(AmyAction.ACK)
                            }
                        }
                    }
                    renderer.onMixChanged()
                    val extra = when (cmd) {
                        is Command.BoardListPresets ->
                            "\n" + BoardSession.presets.listSummary()
                        is Command.BoardSavePreset ->
                            "\nSAVED USER · ${cmd.name}"
                        is Command.BoardLoadPreset ->
                            "\nLOADED · ${cmd.name}"
                        is Command.BoardDeletePreset ->
                            "\nDELETED USER · ${cmd.name}"
                        is Command.AmyPlay ->
                            "\nAMY · ${cmd.actionId.uppercase()}"
                        is Command.AmyAntics ->
                            "\nAMY · ANTICS"
                        is Command.AmyActionsEnabled ->
                            "\nAMY ACTIONS · ${if (cmd.enabled) "ON" else "OFF"}"
                        else -> ""
                    }
                    status.text = mix.statusLine() + extra + "\n" + ElsdApp.CREDIT_SHORT
                }
            },
            onPartial = { partial ->
                runOnUiThread {
                    renderer.setListening(true)
                    status.text = "… $partial\n${ElsdApp.CREDIT_SHORT}"
                }
            },
            onSpeechStart = {
                // Amy leans in as soon as the user starts speaking
                runOnUiThread { renderer.setListening(true) }
            },
            onSpeechEnd = {
                runOnUiThread { renderer.setListening(false) }
            },
        )

        ensurePermissions()
    }

    private fun ensurePermissions() {
        val need = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            need += Manifest.permission.CAMERA
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            need += Manifest.permission.RECORD_AUDIO
        }
        if (need.isEmpty()) {
            startCamera()
            voiceRouter.start()
        } else {
            permissionLauncher.launch(need.toTypedArray())
        }
    }

    private fun startCamera() {
        glView.queueEvent {
            val st = renderer.worldSurfaceTexture() ?: return@queueEvent
            runOnUiThread {
                cameraBus.start(st) {
                    glView.requestRender()
                }
            }
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { c ->
            c.hide(WindowInsetsCompat.Type.systemBars())
            c.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        BoardSession.board.applyToMix(mix)
        glView.onResume()
        // Phase clocks for timed effect envelopes
        glView.queueEvent {
            // tick on draw via renderer callback would be better; approximate here
        }
        cameraBus.resume()
        voiceRouter.start()
        status.post(boardTick)
    }

    private val boardTick = object : Runnable {
        override fun run() {
            val now = System.nanoTime()
            val dt = if (lastBoardTickNs == 0L) 0.016f
            else ((now - lastBoardTickNs) / 1_000_000_000f).coerceIn(0f, 0.1f)
            lastBoardTickNs = now
            BoardSession.board.tick(dt)
            BoardSession.board.applyToMix(mix)
            status.text = mix.statusLine() + "\n" + ElsdApp.CREDIT_SHORT
            status.postDelayed(this, 33L)
        }
    }

    override fun onPause() {
        status.removeCallbacks(boardTick)
        voiceRouter.stop()
        cameraBus.pause()
        glView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        voiceRouter.stop()
        cameraBus.stop()
        super.onDestroy()
    }
}
