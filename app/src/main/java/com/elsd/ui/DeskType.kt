package com.elsd.ui

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.elsd.R

/**
 * TOASTED type ramp — open OFL faces only.
 * See docs/design/TYPE_AND_COLOR.md
 */
object DeskType {
    fun display(context: Context): Typeface =
        ResourcesCompat.getFont(context, R.font.font_desk_display) ?: Typeface.MONOSPACE

    fun label(context: Context): Typeface =
        ResourcesCompat.getFont(context, R.font.font_desk_label) ?: Typeface.MONOSPACE

    fun body(context: Context): Typeface =
        ResourcesCompat.getFont(context, R.font.font_desk_body) ?: Typeface.MONOSPACE

    fun bodyBold(context: Context): Typeface =
        Typeface.create(body(context), Typeface.BOLD)

    fun applyDisplay(tv: TextView) {
        tv.typeface = display(tv.context)
        tv.isAllCaps = true
        tv.letterSpacing = 0.04f
    }

    fun applyLabel(tv: TextView) {
        tv.typeface = label(tv.context)
        tv.isAllCaps = true
        tv.letterSpacing = 0f
    }

    fun applyBody(tv: TextView) {
        tv.typeface = body(tv.context)
        tv.isAllCaps = false
        tv.letterSpacing = 0f
    }

    fun applyCaption(tv: TextView) {
        tv.typeface = body(tv.context)
        tv.isAllCaps = false
        tv.letterSpacing = 0f
    }
}
