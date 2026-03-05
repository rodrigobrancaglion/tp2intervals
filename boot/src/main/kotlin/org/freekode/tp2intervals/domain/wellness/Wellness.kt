package org.freekode.tp2intervals.domain.wellness

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.WellnessType

class Wellness(
    val date: String?,
    val type: WellnessType?,
    val weight: Double?,
    val calories: Double? = null,
    val carbohydrates: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
) {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
    }

    fun filterWellness(requestedTypes: List<BaseType>): Wellness {
        return Wellness(
            date = date,
            type = type,
            weight = if (requestedTypes.contains(WellnessType.WEIGHT)) weight else null,
            calories = if (requestedTypes.contains(WellnessType.CALORIES)) calories else null,
            carbohydrates = if (requestedTypes.contains(WellnessType.CARBOHYDRATES)) carbohydrates else null,
            protein = if (requestedTypes.contains(WellnessType.PROTEIN)) protein else null,
            fat = if (requestedTypes.contains(WellnessType.FAT)) fat else null,
        )
    }
}
