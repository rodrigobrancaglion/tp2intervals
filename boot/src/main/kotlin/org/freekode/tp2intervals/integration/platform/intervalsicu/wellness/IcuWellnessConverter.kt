package org.freekode.tp2intervals.integration.platform.intervalsicu.wellness

import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.intervalsicu.wellness.dto.IcuWellness
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IcuWellnessConverter {
    private var wellnessDTO: IcuWellness? = null
    private var wellness: Wellness? = null

    // Constructor when starting with DTO (from Intervals)
    constructor(wellnessDTO: IcuWellness) {
        this.wellnessDTO = wellnessDTO
    }

    // Constructor when starting with Domain (to send to Intervals)
    constructor(wellness: Wellness?) {
        this.wellness = wellness
    }

    fun toDTO(): IcuWellness {
        val outputFormatter = DateTimeFormatter.ofPattern(Wellness.DATE_FORMAT)

        // 1. Calculate properly formatted date
        val formattedDate = wellness?.date?.let { dateStr ->
            try {
                val date = if (dateStr.contains("T")) {
                    java.time.OffsetDateTime.parse(dateStr).toLocalDate()
                } else {
                    // Extract first 10 characters and parse as LocalDate to validate
                    LocalDate.parse(dateStr.take(10))
                }
                date.format(outputFormatter)
            } catch (e: Exception) {
                // If it fails, fallback to first 10 chars or available string
                dateStr.take(10)
            }
        }

        // 2. Use 'formattedDate' as 'id' in DTO
        return IcuWellness(
            id = formattedDate ?: "",
            weight = wellness?.weight,
            kcalConsumed = wellness?.calories?.toInt(),
            carbohydrates = wellness?.carbohydrates,
            protein = wellness?.protein,
            fatTotal = wellness?.fat
        )
    }

    fun toDomain(): Wellness {
        return Wellness(
            date = wellnessDTO?.id,
            type = WellnessType.WEIGHT,
            weight = wellnessDTO?.weight ?: -1.0,
            calories = wellnessDTO?.kcalConsumed?.toDouble(),
            carbohydrates = wellnessDTO?.carbohydrates,
            protein = wellnessDTO?.protein,
            fat = wellnessDTO?.fatTotal
        )
    }
}
