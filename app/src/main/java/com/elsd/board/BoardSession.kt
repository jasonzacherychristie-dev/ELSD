package com.elsd.board

/**
 * Process-wide switchboard so SWITCHBOARD and LIVE share one board.
 */
object BoardSession {
    val board = Switchboard()
    lateinit var presets: PresetStore
        private set

    fun init(store: PresetStore) {
        presets = store
        store.seedFactoryIfEmpty()
    }
}
