package org.freekode.tp2intervals.domain

/**
 * Types related to "other" calendar events such as races.
 * ICU supports RACE_A (priority), RACE_B (secondary), RACE_C (minor).
 */
enum class OtherType(override val title: String) : BaseType {
    RACE_A("Race A"),
    RACE_B("Race B"),
    RACE_C("Race C"),
    ;

    companion object {
        val DEFAULT_LIST = listOf(RACE_A, RACE_B, RACE_C)

        /** Maps an OtherType to the ICU category string. */
        fun toIcuCategory(type: OtherType): String = type.name  // RACE_A, RACE_B, RACE_C
    }
}
