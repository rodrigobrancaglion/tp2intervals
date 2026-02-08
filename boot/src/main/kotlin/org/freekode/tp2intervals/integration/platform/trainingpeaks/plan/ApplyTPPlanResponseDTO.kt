package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan

import java.time.LocalDateTime

data class ApplyTPPlanResponseDTO(
    val appliedPlanId: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
)
