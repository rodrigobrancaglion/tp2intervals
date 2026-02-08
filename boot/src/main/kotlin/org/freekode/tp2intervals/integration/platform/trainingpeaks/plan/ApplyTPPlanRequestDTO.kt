package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan

data class ApplyTPPlanRequestDTO(
    val athleteId: String,
    val planId: String,
    val targetDate: String,
    val startType: String,
)
