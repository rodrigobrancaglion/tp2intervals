package org.freekode.tp2intervals.domain

enum class TrainingType(override val title: String) : BaseType {
    SWIM("Swim"),
    BIKE("Ride"),
    GRAVEL_BIKE("Ride"),
    VIRTUAL_BIKE("Ride"),
    MTB("MTB"),
    RUN("Run"),
    //CROSSTRAIN
    RACE("Race"),
    WORKOUT("Strength"),
    STRENGTH("Strength"),
    WALK("Walk"),
    DAY_OFF("Day_off-note"),
    NOTE("Note"),
    BRICK("Brick"),
    UNKNOWN("Unknown"),
    ;

    companion object Companion {
        /**
         * Returns the Enum object based on its property name (e.g., "GRAVEL_RIDE").
         * It matches the string exactly with the enum constant name.
         */
        fun getByValue(value: String?): TrainingType {
            if (value == null) return UNKNOWN
            val cleanValue = value.trim()

            return TrainingType.entries.find {
                it.name.equalsIgnoreCase(cleanValue) || it.title.equalsIgnoreCase(cleanValue)
            } ?: UNKNOWN
        }

        // Helper para facilitar a leitura
        private fun String.equalsIgnoreCase(other: String) = this.equals(other, ignoreCase = true)

        val DEFAULT_LIST = listOf(
            BIKE,
            VIRTUAL_BIKE,
            MTB,
            RUN,
            SWIM,
            DAY_OFF,
            NOTE,
            BRICK
        )
    }

    /**
     * Extension to check if the workout type matches a partial string filter.
     */
    fun matchesAny(types: List<BaseType>): Boolean {
        return types.any { filterType ->
            val filterTitle = filterType.title
            val enumTitle = this.title

            // Cross-contains: matches if enum is part of filter OR filter is part of enum
            // This covers cases like "NOTE" vs "Day-off-note"
            filterTitle.contains(enumTitle, ignoreCase = true) ||
                    enumTitle.contains(filterTitle, ignoreCase = true)
        }
    }

}
