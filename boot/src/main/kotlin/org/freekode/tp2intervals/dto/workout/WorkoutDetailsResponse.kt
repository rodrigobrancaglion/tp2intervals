package org.freekode.tp2intervals.dto.workout

import org.freekode.tp2intervals.domain.ExternalData

class WorkoutDetailsResponse(
    val name: String,
    val duration: String?,
    val tssPlanned: Int?,
    val externalData: ExternalData,
)