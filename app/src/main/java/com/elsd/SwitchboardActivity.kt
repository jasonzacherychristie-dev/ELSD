package com.elsd

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.elsd.board.BoardSession
import com.elsd.board.BoardVerb
import com.elsd.board.EffectId
import com.elsd.board.EffectLayer
import com.elsd.board.PresetStore
import com.elsd.ui.DeskType

/**
 * Switchboard — root is a **6-panel grid** (no scroll).
 * Subpages may scroll. Voice still uses full BoardVerb labels.
 *
 * Home panels: ADD · LAYERS · PRESETS · RANDOM · CLEAR · LIVE
 */
class SwitchboardActivity : ComponentActivity() {

    private lateinit var pageRoot: FrameLayout
    private lateinit var homeView: LinearLayout
    private lateinit var subScroll: ScrollView
    private lateinit var subRoot: LinearLayout
    private lateinit var title: TextView
    private lateinit var listHost: LinearLayout
    private lateinit var actionHost: LinearLayout
    private lateinit var statusLine: TextView

    private var drillId: EffectId? = null
    private var mode: Mode = Mode.BOARD

    private enum class Mode {
        BOARD, ADD_PICK, LAYERS, DRILL, PRESETS, PRESET_SAVE, TOOLS
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BoardSession.presetsReady) {
            BoardSession.init(PresetStore(this))
        }

        pageRoot = FrameLayout(this).apply { setBackgroundColor(BG) }

        // —— HOME (no scroll, fills screen) ——
        homeView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12))
            setBackgroundColor(BG)
        }
        statusLine = TextView(this).apply {
            setTextColor(DIM)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            DeskType.applyCaption(this)
            maxLines = 1
            setPadding(0, 0, 0, dp(8))
        }
        homeView.addView(statusLine)
        homeView.addView(
            TextView(this).apply {
                text = "ELSD"
                setTextColor(CREAM)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                DeskType.applyDisplay(this)
                setPadding(0, 0, 0, dp(10))
            },
        )
        homeView.addView(buildSixGrid(), LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f,
        ))

        // —— SUBPAGES (may scroll) ——
        subRoot = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12))
        }
        title = text("").apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            DeskType.applyDisplay(this)
            setTextColor(CREAM)
            setPadding(0, 0, 0, dp(8))
        }
        listHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        actionHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        subRoot.addView(title)
        subRoot.addView(listHost)
        subRoot.addView(space(8))
        subRoot.addView(actionHost)
        subScroll = ScrollView(this).apply {
            setBackgroundColor(BG)
            visibility = View.GONE
            addView(subRoot)
        }

        pageRoot.addView(
            homeView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        pageRoot.addView(
            subScroll,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        setContentView(pageRoot)
        showBoard()
    }

    override fun onResume() {
        super.onResume()
        if (mode == Mode.BOARD) showBoard()
    }

    override fun onBackPressed() {
        if (mode != Mode.BOARD) {
            showBoard()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    // ── HOME: exactly 6 panels ──────────────────────────────────────────

    private fun buildSixGrid(): LinearLayout {
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val cells = listOf(
            "ADD" to { showAddPick() },
            "LAYERS" to { showLayers() },
            "PRESETS" to { showPresetsHub() },
            "RANDOM" to {
                val summary = com.elsd.board.RandomDesk.roll(BoardSession.board)
                Toast.makeText(this, summary, Toast.LENGTH_SHORT).show()
                refreshStatus()
            },
            "CLEAR" to {
                BoardSession.board.clearBoard()
                Toast.makeText(this, "CLEAR", Toast.LENGTH_SHORT).show()
                refreshStatus()
            },
            "LIVE" to {
                startActivity(Intent(this, MainActivity::class.java))
            },
        )
        for (row in 0 until 3) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1f,
                )
            }
            for (col in 0 until 2) {
                val (label, action) = cells[row * 2 + col]
                rowLayout.addView(
                    panelButton(label, accent = label == "LIVE", onClick = action),
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                        setMargins(dp(4), dp(4), dp(4), dp(4))
                    },
                )
            }
            grid.addView(rowLayout)
        }
        return grid
    }

    private fun panelButton(label: String, accent: Boolean, onClick: () -> Unit): Button =
        Button(this).apply {
            text = label
            setAllCaps(true)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(if (accent) CREAM else CREAM)
            setBackgroundColor(if (accent) ON_AIR else PANEL)
            DeskType.applyLabel(this)
            gravity = Gravity.CENTER
            setOnClickListener { onClick() }
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

    private fun showHome() {
        homeView.visibility = View.VISIBLE
        subScroll.visibility = View.GONE
        mode = Mode.BOARD
        drillId = null
        refreshStatus()
    }

    private fun showSub() {
        homeView.visibility = View.GONE
        subScroll.visibility = View.VISIBLE
        subScroll.scrollTo(0, 0)
    }

    private fun showBoard() = showHome()

    private fun refreshStatus() {
        val b = BoardSession.board
        val n = b.layersInOrder().size
        val fps = if (b.targetFps <= 0) "∞" else "${b.targetFps}"
        val drops = if (b.allowDroppedFrames) "DROP" else "HOLD"
        statusLine.text = "${b.presetName.uppercase()}  ·  $n FX  ·  ${fps}fps  ·  $drops"
    }

    // ── LAYERS (+ tools: fps, amy, info) ────────────────────────────────

    private fun showLayers() {
        mode = Mode.LAYERS
        showSub()
        title.text = "LAYERS"
        listHost.removeAllViews()
        actionHost.removeAllViews()

        val board = BoardSession.board
        if (board.layersInOrder().isEmpty()) {
            listHost.addView(text("EMPTY — use ADD").apply {
                setTextColor(DIM)
                setPadding(0, dp(8), 0, dp(8))
            })
        } else {
            board.layersInOrder().forEach { layer ->
                listHost.addView(layerRow(layer))
            }
        }

        actionHost.addView(shortButton("TOOLS") { showTools() })
        actionHost.addView(shortButton("BACK") { showBoard() })
    }

    private fun showTools() {
        mode = Mode.TOOLS
        showSub()
        title.text = "TOOLS"
        listHost.removeAllViews()
        actionHost.removeAllViews()

        val board = BoardSession.board
        listHost.addView(text("FPS").apply {
            setTextColor(AMBER)
            DeskType.applyLabel(this)
            setPadding(0, dp(4), 0, dp(4))
        })
        listHost.addView(frameRateRow())

        listHost.addView(
            shortButton(if (board.allowDroppedFrames) "DROPS ON" else "DROPS OFF") {
                board.allowDroppedFrames = !board.allowDroppedFrames
                showTools()
            },
        )
        listHost.addView(
            shortButton(if (board.amyActionsEnabled) "AMY ON" else "AMY OFF") {
                board.amyActionsEnabled = !board.amyActionsEnabled
                Toast.makeText(
                    this,
                    if (board.amyActionsEnabled) "AMY ON" else "AMY OFF",
                    Toast.LENGTH_SHORT,
                ).show()
                showTools()
            },
        )
        listHost.addView(shortButton("INFO") {
            startActivity(Intent(this, AboutActivity::class.java))
        })
        actionHost.addView(shortButton("BACK") { showLayers() })
    }

    // ── PRESETS ─────────────────────────────────────────────────────────

    private fun showPresetsHub() {
        mode = Mode.PRESETS
        showSub()
        title.text = "PRESETS"
        listHost.removeAllViews()
        actionHost.removeAllViews()

        listHost.addView(text("USER").apply {
            setTextColor(LIME)
            DeskType.applyLabel(this)
            setPadding(0, dp(4), 0, dp(6))
        })
        val user = BoardSession.presets.listUser()
        if (user.isEmpty()) {
            listHost.addView(text("none — SAVE").apply {
                setTextColor(DIM)
                setPadding(0, 0, 0, dp(8))
            })
        } else {
            user.forEach { name -> listHost.addView(presetRow(name, userSave = true)) }
        }

        listHost.addView(text("FACTORY").apply {
            setTextColor(AMBER)
            DeskType.applyLabel(this)
            setPadding(0, dp(12), 0, dp(6))
        })
        BoardSession.presets.listFactory().forEach { name ->
            listHost.addView(presetRow(name, userSave = false))
        }

        actionHost.addView(shortButton("SAVE") { showPresetSave() })
        actionHost.addView(shortButton("BACK") { showBoard() })
    }

    private fun presetRow(name: String, userSave: Boolean): View {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(6))
        }
        val short = name.uppercase().let { if (it.length > 14) it.take(12) + "…" else it }
        col.addView(shortButton(if (userSave) "USR $short" else short) {
            if (BoardSession.presets.load(name, BoardSession.board)) {
                Toast.makeText(this, "LOAD $name", Toast.LENGTH_SHORT).show()
                showBoard()
            }
        })
        if (userSave) {
            col.addView(shortButton("DEL $short", danger = true) {
                BoardSession.presets.deleteUser(name)
                Toast.makeText(this, "DEL $name", Toast.LENGTH_SHORT).show()
                showPresetsHub()
            })
        }
        return col
    }

    private fun frameRateRow(): View {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf(12, 16, 24, 30, 60, 0).forEach { fps ->
            row.addView(Button(this).apply {
                text = if (fps == 0) "∞" else "$fps"
                setTextColor(BG)
                setBackgroundColor(CREAM)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setOnClickListener {
                    BoardSession.board.targetFps = fps
                    BoardSession.board.framerateHardLocked = (fps == 0)
                    Toast.makeText(
                        this@SwitchboardActivity,
                        if (fps == 0) "FPS ∞" else "FPS $fps",
                        Toast.LENGTH_SHORT,
                    ).show()
                    showTools()
                }
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                lp.marginEnd = dp(3)
                layoutParams = lp
            })
        }
        return row
    }

    private fun layerRow(layer: EffectLayer): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(if (layer.enabled) ROW_ON else ROW_OFF)
            setPadding(dp(10), dp(12), dp(10), dp(12))
        }
        row.addView(text(if (layer.enabled) "ON" else "OFF").apply {
            setTextColor(if (layer.enabled) LIME else DIM)
            DeskType.applyLabel(this)
            setPadding(0, 0, dp(10), 0)
        })
        row.addView(text(layer.id.label).apply {
            setTextColor(CREAM)
            DeskType.applyLabel(this)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        row.setOnClickListener { showDrill(layer.id) }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(row)
            addView(space(4))
        }
    }

    private fun showAddPick() {
        mode = Mode.ADD_PICK
        showSub()
        title.text = "ADD"
        listHost.removeAllViews()
        actionHost.removeAllViews()
        EffectId.catalog1_0().groupBy { it.family }.forEach { (family, ids) ->
            listHost.addView(text(family.name).apply {
                setTextColor(AMBER)
                DeskType.applyLabel(this)
                setPadding(0, dp(8), 0, dp(4))
            })
            ids.forEach { id ->
                listHost.addView(shortButton(id.label) {
                    BoardSession.board.addEffect(id)
                    Toast.makeText(this, "ADD ${id.label}", Toast.LENGTH_SHORT).show()
                    showDrill(id)
                })
            }
        }
        actionHost.addView(shortButton("BACK") { showBoard() })
    }

    private fun showDrill(id: EffectId) {
        mode = Mode.DRILL
        showSub()
        drillId = id
        val layer = BoardSession.board.get(id) ?: BoardSession.board.addEffect(id)
        title.text = layer.id.label
        listHost.removeAllViews()
        actionHost.removeAllViews()

        listHost.addView(text(
            "${if (layer.enabled) "ON" else "OFF"}  ·  IN ${layer.fadeInSec}s  OUT ${layer.fadeOutSec}s" +
                if (layer.phaseEnabled) "  ·  PH ${layer.phaseSec}s" else "",
        ).apply {
            setTextColor(DIM)
            DeskType.applyBody(this)
            setPadding(0, 0, 0, dp(10))
        })

        actionHost.addView(shortButton("TOGGLE") {
            BoardSession.board.toggleEffect(id)
            showDrill(id)
        })
        actionHost.addView(shortButton("REMOVE", danger = true) {
            BoardSession.board.removeEffect(id)
            Toast.makeText(this, "REMOVE", Toast.LENGTH_SHORT).show()
            showLayers()
        })
        actionHost.addView(timeRow("IN", listOf(0.5f, 1f, 2f, 4f)) { sec ->
            BoardSession.board.setFadeIn(id, sec)
            showDrill(id)
        })
        actionHost.addView(timeRow("OUT", listOf(0.5f, 1f, 2f, 4f)) { sec ->
            BoardSession.board.setFadeOut(id, sec)
            showDrill(id)
        })
        actionHost.addView(timeRow("PHASE", listOf(4f, 8f, 12f, 20f)) { sec ->
            BoardSession.board.setPhaseTime(id, sec)
            showDrill(id)
        })
        actionHost.addView(shortButton(if (layer.phaseEnabled) "PH OFF" else "PH ON") {
            BoardSession.board.setPhaseEnabled(id, !layer.phaseEnabled)
            showDrill(id)
        })

        if (id == EffectId.MANDELBROT || id == EffectId.JULIA) {
            actionHost.addView(text("ZOOM ${layer.rate}").apply {
                setTextColor(AMBER)
                DeskType.applyLabel(this)
                setPadding(0, dp(8), 0, dp(4))
            })
            actionHost.addView(timeRow("ZOOM", listOf(0.35f, 0.75f, 1f, 1.5f, 2.5f, 4f)) { r ->
                layer.rate = r
                showDrill(id)
            })
            val keyRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            listOf(0 to "FULL", 1 to "DARK", 2 to "BRIT", 3 to "CHR").forEach { (km, label) ->
                keyRow.addView(Button(this).apply {
                    text = label
                    setTextColor(BG)
                    setBackgroundColor(
                        if (BoardSession.board.fractalKeyMode == km) LIME else CREAM,
                    )
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    setOnClickListener {
                        BoardSession.board.fractalKeyMode = km
                        showDrill(id)
                    }
                    val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    lp.marginEnd = dp(3)
                    layoutParams = lp
                })
            }
            actionHost.addView(keyRow)
        }

        actionHost.addView(shortButton("BACK") { showLayers() })
    }

    private fun showPresetSave() {
        mode = Mode.PRESET_SAVE
        showSub()
        title.text = "SAVE"
        listHost.removeAllViews()
        actionHost.removeAllViews()
        val input = EditText(this).apply {
            hint = "NAME"
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
        actionHost.addView(shortButton("SAVE") {
            val name = input.text?.toString().orEmpty()
            val saved = BoardSession.presets.saveUser(name, BoardSession.board)
            Toast.makeText(this, "SAVE $saved", Toast.LENGTH_SHORT).show()
            showPresetsHub()
        })
        actionHost.addView(shortButton("BACK") { showPresetsHub() })
    }

    private fun timeRow(label: String, options: List<Float>, onPick: (Float) -> Unit): View {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(text(label).apply {
            setTextColor(AMBER)
            DeskType.applyLabel(this)
            setPadding(0, dp(6), 0, dp(2))
        })
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        options.forEach { sec ->
            row.addView(Button(this).apply {
                text = if (sec == sec.toInt().toFloat()) "${sec.toInt()}" else "$sec"
                setTextColor(BG)
                setBackgroundColor(CREAM)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setOnClickListener { onPick(sec) }
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                lp.marginEnd = dp(3)
                layoutParams = lp
            })
        }
        col.addView(row)
        return col
    }

    private fun shortButton(
        label: String,
        danger: Boolean = false,
        onClick: () -> Unit,
    ): Button = Button(this).apply {
        text = label
        setAllCaps(true)
        setTextColor(CREAM)
        setBackgroundColor(if (danger) ON_AIR else PANEL_HI)
        DeskType.applyLabel(this)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        setOnClickListener { onClick() }
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        lp.topMargin = dp(4)
        layoutParams = lp
    }

    private fun text(s: String) = TextView(this).apply {
        text = s
        DeskType.applyBody(this)
    }

    private fun space(h: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(h))
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private val BG get() = ContextCompat.getColor(this, R.color.elsd_void)
    private val CREAM get() = ContextCompat.getColor(this, R.color.elsd_cream)
    private val ON_AIR get() = ContextCompat.getColor(this, R.color.elsd_on_air)
    private val LIME get() = ContextCompat.getColor(this, R.color.elsd_listen)
    private val AMBER get() = ContextCompat.getColor(this, R.color.elsd_amber)
    private val DIM get() = ContextCompat.getColor(this, R.color.elsd_cream_dim)
    private val ROW_ON get() = ContextCompat.getColor(this, R.color.elsd_panel_hi)
    private val ROW_OFF get() = ContextCompat.getColor(this, R.color.elsd_panel)
    private val PANEL get() = ContextCompat.getColor(this, R.color.elsd_panel)
    private val PANEL_HI get() = ContextCompat.getColor(this, R.color.elsd_panel_hi)
}
