package org.freekode.tp2intervals.domain

enum class ActivitiesType(override val title: String, override val category: CategoryType) : BaseType {
    //ACTIVITY
    RPE("RPE",      CategoryType.METRICS),
    FEEL("Feel",    CategoryType.METRICS),
    NOTES("Notes",  CategoryType.NOTES_COMMENTS),
    ;

    companion object {
        val DEFAULT_LIST = listOf(RPE, FEEL)
    }

}
