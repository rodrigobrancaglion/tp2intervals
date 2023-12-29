package org.freekode.tp2intervals.domain.workout

import org.freekode.tp2intervals.domain.TrainingType
import java.time.Duration
import java.time.LocalDate

data class Workout(
    val date: LocalDate,
    val type: TrainingType,
    val title: String,
    val duration: Duration?,
    val load: Double?,
    val description: String?,
    val steps: List<WorkoutStep>,
    val externalContent: String?
)
