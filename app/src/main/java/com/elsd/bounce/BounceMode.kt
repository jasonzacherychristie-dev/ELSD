package com.elsd.bounce

enum class BounceMode {
    /** Spirited 2D checker disc — default pilot. */
    FLAT,

    /** Checker sphere in stereo space. */
    SPATIAL,

    /** Background pip / hidden; mix continues. */
    MUTED,
}
