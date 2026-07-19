package com.elsd

import android.app.Application
import com.elsd.board.BoardSession
import com.elsd.board.PresetStore

/**
 * ELSD — Electronic LSD
 *
 * Co-implemented with Grok (xAI). Project lead: Jason Z. Christie.
 * License: MIT (see LICENSE / NOTICE).
 */
class ElsdApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        BoardSession.init(PresetStore(this))
    }

    companion object {
        lateinit var instance: ElsdApp
            private set

        const val CREDIT_SHORT = "Built with Grok (xAI) — Jason Z. Christie"
        const val CREDIT_LONG =
            "ELSD is a Jason Z. Christie project, co-implemented with Grok by xAI."
    }
}
