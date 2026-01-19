package org.freekode.tp2intervals.domain

enum class FeelingType(override val title: String, override val category: CategoryType) : BaseType {
    //FEELING
    STRONG("Strong",    CategoryType.METRICS),
    GOOD("Good",        CategoryType.METRICS),
    NORMAL("Normal",    CategoryType.METRICS),
    POOR("Poor",        CategoryType.METRICS),
    WEAK("Weak",        CategoryType.METRICS),
    ;

}
