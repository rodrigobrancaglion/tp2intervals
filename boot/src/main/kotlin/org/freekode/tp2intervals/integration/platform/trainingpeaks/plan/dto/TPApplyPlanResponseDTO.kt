package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto

import java.time.LocalDateTime

data class TPApplyPlanResponseDTO(
    val appliedPlanId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
)
