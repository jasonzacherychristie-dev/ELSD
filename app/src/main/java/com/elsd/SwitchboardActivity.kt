package com.elsd

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import com.elsd.board.BoardSession
import com.elsd.board.BoardVerb
import com.elsd.board.EffectId
import com.elsd.board.EffectLayer
import com.elsd.board.PresetStore

/**
 * Single switchboard UI page — drill down into an effect.
 * Vocabulary: ADD EFFECT, REMOVE EFFECT, ADD EFFECT TIME, SAVE PRESET, …
 */
class SwitchboardActivity : ComponentActivity() {

    private lateinit var root: LinearLayout
    private lateinit var title: TextView
    private lateinit var listHost: LinearLayout
    private lateinit var actionHost: LinearLayout
    private var drillId: EffectId? = null
    private var mode: Mode = Mode.BOARD

    private enum class Mode { BOARD, ADD_PICK, DRILL, PRESETS, PRESET_SAVE, PRESET_LOAD }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BoardSession::presets.isInitialized) {
            BoardSession.init(PresetStore(this))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(BG)
        }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16))
        }
        scroll.addView(root)
        setContentView(scroll)

        title = text(BoardVerb.ADD_EFFECT.label).apply {
            // placeholder; refresh sets real title
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(CREAM)
        }
        listHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        actionHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        root.addView(title)
        root.addView(text(ElsdApp.CREDIT_SHORT).apply {
            setTextColor(DIM)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setPadding(0, dp(4), 0, dp(12))
        })
        root.addView(listHost)
        root.addView(space(12))
        root.addView(actionHost)

        showBoard()
    }

    override fun onResume() {
        super.onResume()
        if (mode == Mode.BOARD) showBoard()
    }

    private fun showBoard() {
        mode = Mode.BOARD
        drillId = null
        title.text = "SWITCHBOARD"
        listHost.removeAllViews()
        actionHost.removeAllViews()

        val board = BoardSession.board
        if (board.layersInOrder().isEmpty()) {
            listHost.addView(text("NO EFFECTS — USE ADD EFFECT").apply {
                setTextColor(DIM)
                setPadding(0, dp(8), 0, dp(8))
            })
        } else {
            board.layersInOrder().forEach { layer ->
                listHost.addView(layerRow(layer))
            }
        }

        val fpsLabel = if (board.targetFps <= 0) "UNLOCK" else "${board.targetFps} FPS"
        val dropLabel = if (board.allowDroppedFrames) "DROPPED FRAMES ON" else "DROPPED FRAMES OFF"
        listHost.addView(text("FRAME  $fpsLabel  ·  $dropLabel").apply {
            setTextColor(AMBER)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(10))
        })

        // PRESETS / USER SAVES — first-class switchboard section
        val userCount = BoardSession.presets.listUser().size
        val factoryCount = BoardSession.presets.listFactory().size
        listHost.addView(text("PRESETS / USER SAVES  ·  factory $factoryCount  ·  user $userCount").apply {
            setTextColor(LIME)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(6))
        })
        listHost.addView(text("Active: ${board.presetName.uppercase()}").apply {
            setTextColor(DIM)
            setPadding(0, 0, 0, dp(8))
        })

        actionHost.addView(verbButton(BoardVerb.PRESETS) { showPresetsHub() })
        actionHost.addView(verbButton(BoardVerb.SAVE_PREFS) { showPresetSave() })
        actionHost.addView(verbButton(BoardVerb.ADD_EFFECT) { showAddPick() })
        actionHost.addView(verbButton(BoardVerb.RANDOM) {
            val summary = com.elsd.board.RandomDesk.roll(BoardSession.board)
            Toast.makeText(this, summary, Toast.LENGTH_LONG).show()
            showBoard()
        })
        actionHost.addView(frameRateRow())
        actionHost.addView(
            verbButton(
                if (board.allowDroppedFrames) BoardVerb.DISABLE_DROPPED_FRAMES
                else BoardVerb.ENABLE_DROPPED_FRAMES,
            ) {
                board.allowDroppedFrames = !board.allowDroppedFrames
                Toast.makeText(
                    this,
                    if (board.allowDroppedFrames) BoardVerb.ENABLE_DROPPED_FRAMES.label
                    else BoardVerb.DISABLE_DROPPED_FRAMES.label,
                    Toast.LENGTH_SHORT,
                ).show()
                showBoard()
            },
        )
        actionHost.addView(verbButton(BoardVerb.CLEAR_BOARD) {
            board.clearBoard()
            Toast.makeText(this, BoardVerb.CLEAR_BOARD.label, Toast.LENGTH_SHORT).show()
            showBoard()
        })
        actionHost.addView(verbButton(BoardVerb.GO_LIVE) {
            startActivity(Intent(this, MainActivity::class.java))
        })
    }

    private fun showPresetsHub() {
        mode = Mode.PRESETS
        title.text = "PRESETS / USER SAVES"
        listHost.removeAllViews()
        actionHost.removeAllViews()

        listHost.addView(text("USER SAVES").apply {
            setTextColor(LIME)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(6))
        })
        val user = BoardSession.presets.listUser()
        if (user.isEmpty()) {
            listHost.addView(text("NO USER SAVES — USE SAVE PREFS").apply {
                setTextColor(DIM)
                setPadding(0, 0, 0, dp(8))
            })
        } else {
            user.forEach { name ->
                listHost.addView(presetRow(name, userSave = true))
            }
        }

        listHost.addView(text("FACTORY").apply {
            setTextColor(AMBER)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(12), 0, dp(6))
        })
        BoardSession.presets.listFactory().forEach { name ->
            listHost.addView(presetRow(name, userSave = false))
        }

        actionHost.addView(verbButton(BoardVerb.SAVE_PREFS) { showPresetSave() })
        actionHost.addView(verbButton(BoardVerb.LIST_PRESETS) {
            Toast.makeText(this, BoardSession.presets.listSummary(), Toast.LENGTH_LONG).show()
        })
        actionHost.addView(verbButton(BoardVerb.BACK) { showBoard() })
    }

    private fun presetRow(name: String, userSave: Boolean): View {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(8))
        }
        col.addView(verbButton(if (userSave) "LOAD USER · ${name.uppercase()}" else "LOAD · ${name.uppercase()}") {
            if (BoardSession.presets.load(name, BoardSession.board)) {
                Toast.makeText(this, "LOADED $name", Toast.LENGTH_SHORT).show()
                showBoard()
            }
        })
        if (userSave) {
            col.addView(Button(this).apply {
                text = "DELETE · ${name.uppercase()}"
                setTextColor(CREAM)
                setBackgroundColor(Color.parseColor("#4A1518"))
                setOnClickListener {
                    BoardSession.presets.deleteUser(name)
                    Toast.makeText(this@SwitchboardActivity, "DELETED $name", Toast.LENGTH_SHORT).show()
                    showPresetsHub()
                }
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                lp.topMargin = dp(4)
                layoutParams = lp
            })
        }
        return col
    }

    private fun frameRateRow(): View {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(text(BoardVerb.SET_FRAMERATE.label).apply {
            setTextColor(AMBER)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(4))
        })
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val options = listOf(12, 16, 24, 30, 60, 0)
        options.forEach { fps ->
            row.addView(Button(this).apply {
                text = if (fps == 0) "UNLOCK" else "$fps"
                setTextColor(BG)
                setBackgroundColor(CREAM)
                setOnClickListener {
                    BoardSession.board.targetFps = fps
                    // UNLOCK hard-locks so ADD CINEMA won't stomp; numeric set allows soft cinema again after
                    BoardSession.board.framerateHardLocked = (fps == 0)
                    Toast.makeText(
                        this@SwitchboardActivity,
                        if (fps == 0) BoardVerb.UNLOCK_FRAMERATE.label
                        else "${BoardVerb.SET_FRAMERATE.label} $fps",
                        Toast.LENGTH_SHORT,
                    ).show()
                    showBoard()
                }
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                lp.marginEnd = dp(4)
                layoutParams = lp
            })
        }
        col.addView(row)
        return col
    }

    private fun layerRow(layer: EffectLayer): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8))
            setBackgroundColor(if (layer.enabled) ROW_ON else ROW_OFF)
        }
        val lamp = text(if (layer.enabled) "ON" else "OFF").apply {
            setTextColor(if (layer.enabled) LIME else DIM)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, 0, dp(12), 0)
        }
        val name = text(layer.id.label).apply {
            setTextColor(CREAM)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val times = text(
            "IN ${layer.fadeInSec}s  OUT ${layer.fadeOutSec}s" +
                if (layer.phaseEnabled) "  PHASE ${layer.phaseSec}s" else "",
        ).apply {
            setTextColor(DIM)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
        }
        row.addView(lamp)
        row.addView(name)
        row.addView(times)
        row.setOnClickListener { showDrill(layer.id) }
        row.setPadding(dp(8), dp(12), dp(8), dp(12))
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(row)
            addView(space(6))
        }
        return wrap
    }

    private fun showAddPick() {
        mode = Mode.ADD_PICK
        title.text = BoardVerb.ADD_EFFECT.label
        listHost.removeAllViews()
        actionHost.removeAllViews()
        EffectId.catalog1_0().groupBy { it.family }.forEach { (family, ids) ->
            listHost.addView(text(family.name).apply {
                setTextColor(AMBER)
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                setPadding(0, dp(10), 0, dp(4))
            })
            ids.forEach { id ->
                listHost.addView(verbButton(id.label) {
                    BoardSession.board.addEffect(id)
                    Toast.makeText(this, "${BoardVerb.ADD_EFFECT.label} ${id.label}", Toast.LENGTH_SHORT).show()
                    showDrill(id)
                })
            }
        }
        actionHost.addView(verbButton(BoardVerb.BACK) { showBoard() })
    }

    private fun showDrill(id: EffectId) {
        mode = Mode.DRILL
        drillId = id
        val layer = BoardSession.board.get(id) ?: BoardSession.board.addEffect(id)
        title.text = layer.id.label
        listHost.removeAllViews()
        actionHost.removeAllViews()

        listHost.addView(text(
            "ON=${layer.enabled}  IN=${layer.fadeInSec}s  OUT=${layer.fadeOutSec}s\n" +
                "PHASE=${layer.phaseSec}s  PHASE_ON=${layer.phaseEnabled}",
        ).apply {
            setTextColor(DIM)
            setTypeface(Typeface.MONOSPACE, Typeface.NORMAL)
            setPadding(0, 0, 0, dp(12))
        })

        actionHost.addView(verbButton(BoardVerb.TOGGLE_EFFECT) {
            BoardSession.board.toggleEffect(id)
            showDrill(id)
        })
        actionHost.addView(verbButton(BoardVerb.REMOVE_EFFECT) {
            BoardSession.board.removeEffect(id)
            Toast.makeText(this, BoardVerb.REMOVE_EFFECT.label, Toast.LENGTH_SHORT).show()
            showBoard()
        })
        actionHost.addView(timeRow(BoardVerb.ADD_EFFECT_TIME.label, listOf(0.5f, 1f, 2f, 4f)) { sec ->
            BoardSession.board.setFadeIn(id, sec)
            showDrill(id)
        })
        actionHost.addView(timeRow(BoardVerb.REMOVE_EFFECT_TIME.label, listOf(0.5f, 1f, 2f, 4f)) { sec ->
            BoardSession.board.setFadeOut(id, sec)
            showDrill(id)
        })
        actionHost.addView(timeRow(BoardVerb.PHASE_TIME.label, listOf(4f, 8f, 12f, 20f)) { sec ->
            BoardSession.board.setPhaseTime(id, sec)
            showDrill(id)
        })
        actionHost.addView(verbButton(BoardVerb.PHASE_ON) {
            BoardSession.board.setPhaseEnabled(id, true)
            showDrill(id)
        })
        actionHost.addView(verbButton(BoardVerb.PHASE_OFF) {
            BoardSession.board.setPhaseEnabled(id, false)
            showDrill(id)
        })

        // Mandelbrot / Julia: adjustable zoom rate + chromakey into FOV
        if (id == EffectId.MANDELBROT || id == EffectId.JULIA) {
            actionHost.addView(text("ZOOM RATE  (now ${layer.rate})").apply {
                setTextColor(AMBER)
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                setPadding(0, dp(10), 0, dp(4))
            })
            actionHost.addView(timeRow("SET ZOOM RATE", listOf(0.35f, 0.75f, 1f, 1.5f, 2.5f, 4f)) { r ->
                layer.rate = r
                showDrill(id)
            })
            actionHost.addView(text("FRACTAL KEY INTO FOV").apply {
                setTextColor(AMBER)
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                setPadding(0, dp(10), 0, dp(4))
            })
            val keyRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            listOf(
                0 to "FULL",
                1 to "DARKS",
                2 to "BRIGHTS",
                3 to "CHROMA",
            ).forEach { (mode, label) ->
                keyRow.addView(Button(this).apply {
                    text = label
                    setTextColor(BG)
                    setBackgroundColor(
                        if (BoardSession.board.fractalKeyMode == mode) LIME else CREAM,
                    )
                    setOnClickListener {
                        BoardSession.board.fractalKeyMode = mode
                        showDrill(id)
                    }
                    val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    lp.marginEnd = dp(4)
                    layoutParams = lp
                })
            }
            actionHost.addView(keyRow)
            actionHost.addView(text("CHROMA: green walls/sky → infinity. ADD TRAIL stacks afterimage.").apply {
                setTextColor(DIM)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setPadding(0, dp(6), 0, dp(4))
            })
        }

        actionHost.addView(verbButton(BoardVerb.BACK) { showBoard() })
    }

    private fun showPresetSave() {
        mode = Mode.PRESET_SAVE
        title.text = "SAVE PREFS / USER SAVE"
        listHost.removeAllViews()
        actionHost.removeAllViews()
        listHost.addView(text("Saves to USER (not factory). Voice: save prefs walk").apply {
            setTextColor(DIM)
            setPadding(0, 0, 0, dp(8))
        })
        val input = EditText(this).apply {
            hint = "USER SAVE NAME"
            setTextColor(CREAM)
            setHintTextColor(DIM)
            setBackgroundColor(ROW_OFF)
            setPadding(dp(12))
            val suggest = BoardSession.board.presetName.let {
                if (it == "untitled" || it == "random" || it == "clear") "my_look" else it
            }
            setText(suggest)
        }
        listHost.addView(input)
        actionHost.addView(verbButton(BoardVerb.SAVE_PREFS) {
            val name = input.text?.toString().orEmpty()
            val saved = BoardSession.presets.saveUser(name, BoardSession.board)
            Toast.makeText(this, "USER SAVE · $saved", Toast.LENGTH_SHORT).show()
            showPresetsHub()
        })
        actionHost.addView(verbButton(BoardVerb.BACK) { showPresetsHub() })
    }

    private fun timeRow(label: String, options: List<Float>, onPick: (Float) -> Unit): View {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(text(label).apply {
            setTextColor(AMBER)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(4))
        })
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        options.forEach { sec ->
            row.addView(Button(this).apply {
                text = "${sec}s"
                setTextColor(BG)
                setBackgroundColor(CREAM)
                setOnClickListener { onPick(sec) }
                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                lp.marginEnd = dp(4)
                layoutParams = lp
            })
        }
        col.addView(row)
        return col
    }

    private fun verbButton(verb: BoardVerb, onClick: () -> Unit): Button =
        verbButton(verb.label, onClick)

    private fun verbButton(label: String, onClick: () -> Unit): Button =
        Button(this).apply {
            text = label
            setAllCaps(true)
            setTextColor(BG)
            setBackgroundColor(RED)
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setOnClickListener { onClick() }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            lp.topMargin = dp(6)
            layoutParams = lp
        }

    private fun text(s: String) = TextView(this).apply { text = s }

    private fun space(h: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(h))
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    companion object {
        private val BG = Color.parseColor("#0D0D0F")
        private val CREAM = Color.parseColor("#F5EDE0")
        private val RED = Color.parseColor("#DC1F24")
        private val LIME = Color.parseColor("#8CF258")
        private val AMBER = Color.parseColor("#E6B84D")
        private val DIM = Color.parseColor("#8A8580")
        private val ROW_ON = Color.parseColor("#1A2218")
        private val ROW_OFF = Color.parseColor("#18181C")
    }
}
