package org.freekode.tp2intervals.infrastructure.platform.intervalsicu

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.infrastructure.platform.intervalsicu.workout.IntervalsTrainingTypeMapper
import java.time.LocalDateTime

class IntervalsActivityDTO(
    val id: String,
    val paired_event_id: Long?,
    val name: String?,
    val description: String?,
    val start_date_local: LocalDateTime,
    val type: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
    val icu_rpe: Int?,
    val feel: Int?
) {
    constructor(
        icu_rpe: Int?,
        feel: Int?) :
            this("", null, null, null, LocalDateTime.now(), null, null, null, icu_rpe, feel)

    fun mapType(): TrainingType = type?.let { IntervalsTrainingTypeMapper.getByIntervalsType(it) } ?: TrainingType.UNKNOWN
}
