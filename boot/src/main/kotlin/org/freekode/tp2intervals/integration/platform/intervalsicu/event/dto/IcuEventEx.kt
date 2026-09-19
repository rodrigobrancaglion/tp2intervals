package org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.integration.platform.intervalsicu.mapper.IcuTrainingTypeMapper
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IcuWorkoutDocDTO
import java.time.Duration

data class IcuEventEx(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val start_date_local: String,
    val category: String,
    val type: String?,
    val moving_time: Long?,
    val distance: Double? = null,
    val icu_training_load: Int? = null,
    val icu_intensity: Double? = null,
    val workout_doc: IcuWorkoutDocDTO? = null,
    val paired_activity_id: String? = null,
    val attachments: List<Any>? = emptyList(),
    val end_date_local: String? = null,
    val entered: Boolean = false,
    val indoor: Boolean = false,
) {
    fun getTrainingType(): TrainingType {
        // 1. Try mapping by the 'type' field.
        val typeFromField = type?.let { IcuTrainingTypeMapper.getByValue(it) } ?: TrainingType.UNKNOWN

        // 2. If it's UNKNOWN (and not explicitly "Other"), try mapping it using the 'category' field.
        if (typeFromField == TrainingType.UNKNOWN && type != "Other") {
            return IcuTrainingTypeMapper.getByValue(category)
        }

        return typeFromField
    }

    fun getSubType(): TrainingType = IcuTrainingTypeMapper.getByValue(type)

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