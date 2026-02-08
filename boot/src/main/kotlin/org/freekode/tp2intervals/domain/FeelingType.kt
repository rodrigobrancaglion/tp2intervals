package org.freekode.tp2intervals.domain

enum class FeelingType(override val title: String) : BaseType {
    STRONG("Strong"),
    GOOD("Good"),
    NORMAL("Normal"),
    POOR("Poor"),
    WEAK("Weak"),
    ;

}
