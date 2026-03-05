package org.freekode.tp2intervals.integration.platform.myfitnesspal.wellness

import org.freekode.tp2intervals.domain.wellness.Wellness

class MfpWellnessConverter(private val dto: MfpNutritionDTO) {

    fun toDomain(): Wellness {
        return Wellness(
            date = dto.date,
            type = null,
            weight = null,
            calories = dto.calories,
            carbohydrates = dto.carbohydrates,
            protein = dto.protein,
            fat = dto.fat
        )
    }
}
