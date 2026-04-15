package org.freekode.tp2intervals.integration.platform.strava.activity.dto

import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.integration.platform.intervalsicu.mapper.IntervalsTrainingTypeMapper
import java.time.LocalDateTime

/**
 * DTO for a Strava activity response.
 */
//TODO("Not yet implemented")
data class StravaActivityResponseDTO(
    val id: Long,
    val name: String,
    val start_date_local: LocalDateTime,
    val type: String,
    val sport_type: String?,
    val distance: Double?,
    val moving_time: Int?,
    val elapsed_time: Int?,
    val total_elevation_gain: Double?,
    val trainer: Boolean?,
    val commute: Boolean?,
) {
    fun mapType(): TrainingType =
        IntervalsTrainingTypeMapper.getByValue(sport_type ?: type)
}
