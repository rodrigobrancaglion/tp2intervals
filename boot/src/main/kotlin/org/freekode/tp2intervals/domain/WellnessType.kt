package org.freekode.tp2intervals.domain

enum class WellnessType(override val title: String, override val category: CategoryType) : BaseType {
    //WELLNESS (Metrics)
    WEIGHT("Weight Wellness", CategoryType.WELLNESS),
    ;

    companion object {
        val DEFAULT_LIST = listOf(WEIGHT)
    }

}
