package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto

import java.time.LocalDate

data class TPPlanDTO(
    val planId: String,
    val title: String,
    val workoutCount: Int,
    val startDate: LocalDate?,
)
