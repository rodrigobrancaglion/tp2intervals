package org.freekode.tp2intervals.domain

enum class WellnessType(override val title: String) : BaseType {
    WEIGHT("Weight Wellness"),
    CALORIES("Calories"),
    CARBOHYDRATES("Carbohydrates"),
    PROTEIN("Protein"),
    FAT("Fat"),
    ;

    companion object {
        val DEFAULT_LIST = listOf(WEIGHT)
        val MFP_LIST = listOf(CALORIES, CARBOHYDRATES, PROTEIN, FAT)
    }

}
