package org.freekode.tp2intervals.domain

enum class ActivityType(override val title: String) : BaseType {
    ACTIVITY("ACTIVITY"),
    RPE("RPE"),
    FEEL("Feel"),
    NOTES("Notes"),
    ;

    companion object Companion {
        val DEFAULT_LIST = listOf(RPE, FEEL)
    }

}
