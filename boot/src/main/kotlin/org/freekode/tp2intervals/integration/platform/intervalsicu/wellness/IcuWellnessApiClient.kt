package org.freekode.tp2intervals.integration.platform.intervalsicu.wellness

import org.freekode.tp2intervals.integration.platform.intervalsicu.IcuApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.wellness.dto.IcuWellness
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    value = "IntervalsWellnessApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IcuApiClientConfig::class]
)
interface IcuWellnessApiClient {

    @GetMapping("/api/v1/athlete/{athleteId}/wellness?oldest={startDate}&newest={endDate}")
    fun getWellness(
        @PathVariable("athleteId") athleteId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String,
    ): List<IcuWellness>

    @PutMapping("/api/v1/athlete/{athleteId}/wellness")
    fun updateWellness(
        @PathVariable athleteId: String,
        @RequestBody requestDTO: IcuWellness
    )

}
