package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPApiClientConfig
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPApplyPlanRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPApplyPlanResponseDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPPlanDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "TrainingPeaksPlanApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TPApiClientConfig::class]
)
interface TPPlanApiClient {
    @GetMapping("/plans/v1/plans/{planId}")
    fun getPlan(@PathVariable planId: String): TPPlanDTO

    @GetMapping("/plans/v1/plans")
    fun getPlans(): List<TPPlanDTO>

    @PostMapping("plans/v1/commands/applyplan")
    fun applyPlan(@RequestBody tpApplyPlanRequestDTO: List<TPApplyPlanRequestDTO>): List<TPApplyPlanResponseDTO>

    @PostMapping("plans/v1/commands/removeplan")
    fun removePlan(@RequestBody request: Map<String, String>)
}
