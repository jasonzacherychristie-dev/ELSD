package com.elsd

import android.app.Application

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
    }

    companion object {
        lateinit var instance: ElsdApp
            private set

        const val CREDIT_SHORT = "Built with Grok (xAI) — Jason Z. Christie"
        const val CREDIT_LONG =
            "ELSD is a Jason Z. Christie project, co-implemented with Grok by xAI."
    }
}
