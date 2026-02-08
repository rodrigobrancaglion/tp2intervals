package org.freekode.tp2intervals.dto.workout

import org.freekode.tp2intervals.domain.Platform
import java.time.LocalDate

class DeleteWorkoutRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val platform: Platform,
)