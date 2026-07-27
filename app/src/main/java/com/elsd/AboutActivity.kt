package com.elsd

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.elsd.ui.DeskType

/**
 * Splash / info — Praxis house brand, credits, license.
 * Type & color: docs/design/TYPE_AND_COLOR.md
 */
class AboutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val void = color(R.color.elsd_void)
        val cream = color(R.color.elsd_cream)
        val dim = color(R.color.elsd_cream_dim)
        val amber = color(R.color.elsd_amber)
        val listen = color(R.color.elsd_listen)
        val panel = color(R.color.elsd_panel)
        val onAir = color(R.color.elsd_on_air)

        val scroll = ScrollView(this).apply { setBackgroundColor(void) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24))
            gravity = Gravity.CENTER_HORIZONTAL
        }
        scroll.addView(root)
        setContentView(scroll)

        // House brand
        root.addView(
            display(ElsdApp.HOUSE_BRAND, 30f, cream).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(36), 0, dp(4))
            },
        )
        root.addView(
            caption(getString(R.string.house_brand_tag), amber).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(28))
            },
        )

        // Product
        root.addView(
            display("ELSD", 40f, listen).apply {
                gravity = Gravity.CENTER
            },
        )
        root.addView(
            body("Electronic LSD", 15f, cream).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, dp(4))
            },
        )
        root.addView(
            caption(
                "v${BuildConfig.VERSION_NAME}  ·  ${BuildConfig.VERSION_CODE}",
                dim,
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(20))
            },
        )

        root.addView(
            body(getString(R.string.about_mission), 13f, cream).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(24))
            },
        )

        root.addView(section("HOUSE", amber))
        root.addView(body(ElsdApp.HOUSE_BRAND_LINE, 13f, cream))

        root.addView(section("CREDITS", amber))
        root.addView(body(ElsdApp.CREDIT_LONG, 13f, cream))
        root.addView(
            body(ElsdApp.CREDIT_SHORT, 12f, dim).apply {
                setPadding(0, dp(8), 0, 0)
            },
        )

        root.addView(section("LICENSE", amber))
        root.addView(body(getString(R.string.about_license), 13f, cream))

        root.addView(section("OPEN SOURCE", amber))
        root.addView(body(getString(R.string.about_repo), 13f, cream))

        root.addView(section("TYPE", amber))
        root.addView(
            body(
                "Russo One · Share Tech Mono · Space Mono — SIL OFL. " +
                    "No proprietary classic system faces.",
                12f,
                dim,
            ),
        )

        val back = Button(this).apply {
            text = "BACK TO SWITCHBOARD"
            setBackgroundColor(panel)
            setTextColor(listen)
            DeskType.applyLabel(this)
            setOnClickListener { finish() }
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        root.addView(
            back,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = dp(32)
                bottomMargin = dp(16)
            },
        )

        // On-air accent bar (soul, not chrome overload)
        root.addView(
            TextView(this).apply {
                text = " "
                setBackgroundColor(onAir)
                height = dp(3)
            },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(3),
            ).apply { bottomMargin = dp(40) },
        )
    }

    private fun section(t: String, amber: Int): TextView =
        TextView(this).apply {
            text = t
            setTextColor(amber)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            DeskType.applyLabel(this)
            letterSpacing = 0.14f
            setPadding(0, dp(18), 0, dp(6))
        }

    private fun display(t: String, sp: Float, color: Int): TextView =
        TextView(this).apply {
            text = t
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
            DeskType.applyDisplay(this)
            setLineSpacing(0f, 1.05f)
        }

    private fun body(t: String, sp: Float, color: Int): TextView =
        TextView(this).apply {
            text = t
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
            DeskType.applyBody(this)
            setLineSpacing(0f, 1.25f)
        }

    private fun caption(t: String, color: Int): TextView =
        TextView(this).apply {
            text = t
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            DeskType.applyCaption(this)
        }

    private fun color(id: Int): Int = ContextCompat.getColor(this, id)

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
