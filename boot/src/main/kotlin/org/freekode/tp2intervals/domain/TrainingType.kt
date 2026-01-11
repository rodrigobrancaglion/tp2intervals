package org.freekode.tp2intervals.domain

enum class TrainingType(val title: String, val category: CategoryType) {
    BIKE("Ride", CategoryType.WORKOUT),
    MTB("MTB", CategoryType.WORKOUT),
    VIRTUAL_BIKE("Virtual Ride", CategoryType.WORKOUT),
    RUN("Run", CategoryType.WORKOUT),
    SWIM("Swim", CategoryType.WORKOUT),
    WALK("Walk", CategoryType.WORKOUT),
    WEIGHT("Weight", CategoryType.SET_FITNESS),
    NOTE("Note", CategoryType.NOTE),
    BRICK("Brick", CategoryType.NOTE),
    UNKNOWN("Unknown", CategoryType.WORKOUT);

    companion object {
        val DEFAULT_LIST = listOf(BIKE, VIRTUAL_BIKE, MTB, RUN, SWIM, NOTE, BRICK)
    }

}
