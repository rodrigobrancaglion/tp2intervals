package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto

data class TPApplyPlanRequestDTO(
    val athleteId: String,
    val planId: String,
    val targetDate: String,
    val startType: String,
)
