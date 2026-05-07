package org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.integration.platform.intervalsicu.mapper.IntervalsTrainingTypeMapper
import java.time.Duration
import java.time.LocalDateTime

data class IntervalsEventDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val start_date_local: LocalDateTime,
    val category: String,
    val type: String?,
    val moving_time: Long?,
    val distance: Double?,
    val icu_training_load: Int?,
    val icu_intensity: Double?,
    val workout_doc: IntervalsWorkoutDocDTO?,
    val paired_activity_id: String?,
) {

    fun getType(): TrainingType {
        // 1. Try mapping by the 'type' field.
        val typeFromField = type?.let { IntervalsTrainingTypeMapper.getByValue(it) } ?: TrainingType.UNKNOWN

        // 2. If it's UNKNOWN, try mapping it using the 'category' field.
        if (typeFromField == TrainingType.UNKNOWN) {
            return IntervalsTrainingTypeMapper.getByValue(category)
        }

        return typeFromField
    }

    fun getSubType(): TrainingType = IntervalsTrainingTypeMapper.getByValue(type)

    fun mapDuration(): Duration? = moving_time?.let { Duration.ofSeconds(it) }

    /**
     * Returns IF (Intensity Factor).
     * Uses the value from Intervals or calculates it if missing.
     */
    fun getIntensityFactor(): Double {
        // If Intervals already provided the intensity, use it.
        if (icu_intensity != null) return icu_intensity

        // Fallback calculation: IF = sqrt(TSS / (100 * durationInHours))
        val tss = icu_training_load?.toDouble() ?: return 0.0
        val seconds = moving_time?.toDouble() ?: return 0.0

        if (seconds <= 0) return 0.0

        val durationHours = seconds / 3600.0
        return Math.sqrt(tss / (100.0 * durationHours)) * 100
    }
}
