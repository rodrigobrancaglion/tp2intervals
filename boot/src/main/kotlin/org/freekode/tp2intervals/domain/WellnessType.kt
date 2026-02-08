package org.freekode.tp2intervals.domain

enum class WellnessType(override val title: String) : BaseType {
    WEIGHT("Weight Wellness"),
    ;

    companion object {
        val DEFAULT_LIST = listOf(WEIGHT)
    }

}
