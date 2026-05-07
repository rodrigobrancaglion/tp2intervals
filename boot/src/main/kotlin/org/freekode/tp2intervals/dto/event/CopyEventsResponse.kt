package org.freekode.tp2intervals.dto.event

import java.time.LocalDate

data class CopyEventsResponse(
    val copied: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
