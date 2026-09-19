package org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.integration.platform.intervalsicu.mapper.IcuTrainingTypeMapper
import java.time.LocalDateTime

class IcuActivity(
    val id: String,
    val paired_event_id: Long?,
    val name: String?,
    val description: String?,
    val start_date_local: LocalDateTime?,
    val type: String?,
    val moving_time: Long?,
    val icu_training_load: Int?,
    val icu_rpe: Int?,
    val feel: Int?
) {
    constructor(
        paired_event_id: Long?,
        icu_rpe: Int?,
        feel: Int?) :
            this("", paired_event_id, null, null, null, null, null, null, icu_rpe, feel)

    fun mapType(): TrainingType = type?.let { IcuTrainingTypeMapper.getByValue(it) } ?: TrainingType.UNKNOWN
}