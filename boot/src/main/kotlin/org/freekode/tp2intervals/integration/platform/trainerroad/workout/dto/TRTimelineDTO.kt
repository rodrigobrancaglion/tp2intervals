package org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto

import java.time.LocalDate
import java.time.LocalDateTime

class TRTimelineDTO(
    val plannedActivities: List<PlannedActivityDTO> = emptyList(),
    val activities: List<ActivityStateDTO> = emptyList(),
) {
    class PlannedActivityDTO(
        val id: String,
        val date: DateDTO,
        val workoutId: Long?,
    )

    class ActivityStateDTO(
        val id: Long,
        val started: LocalDateTime?,
    )

    class DateDTO(
        val year: Int,
        val month: Int,
        val day: Int,
    ) {
        fun toLocalDate(): LocalDate = LocalDate.of(year, month, day)
    }
}