package org.freekode.tp2intervals.integration.platform.myfitnesspal.wellness.dto

data class MfpNutritionDTO(
    val date: String?,
    val calories: Double? = null,
    val carbohydrates: Double? = null,
    val fat: Double? = null,
    val protein: Double? = null,
    val sodium: Double? = null,
    val sugar: Double? = null,
    val diaryIsComplete: Boolean? = null,
)