package org.freekode.tp2intervals.infrastructure.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.TrainingType
import java.time.Duration
import java.time.LocalDateTime

class IntervalsEventDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val start_date_local: LocalDateTime,
    val category: String,
    val type: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
    val icu_intensity: Double?,
    val workout_doc: IntervalsWorkoutDocDTO?,
    val paired_activity_id: String?,
) {

    fun mapType(): TrainingType {
        // 1. Try mapping by the 'type' field.
        val typeFromField = type?.let { IntervalsTrainingTypeMapper.getByIntervalsType(it) } ?: TrainingType.UNKNOWN

        // 2. If it's UNKNOWN, try mapping it using the 'category' field.
        if (typeFromField == TrainingType.UNKNOWN) {
            return IntervalsTrainingTypeMapper.getByIntervalsType(category)
        }

        return typeFromField
    }

    fun mapDuration(): Duration? = moving_time?.let { Duration.ofSeconds(it) }

    fun isWorkout() = category == "WORKOUT"
}
