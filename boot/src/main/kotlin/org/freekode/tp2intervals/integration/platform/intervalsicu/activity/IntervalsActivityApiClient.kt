package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@FeignClient(
    value = "IntervalsActivityApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IntervalsApiClientConfig::class]
)
interface IntervalsActivityApiClient {

    @GetMapping("/api/v1/athlete/{athleteId}/activities?oldest={startDate}&newest={endDate}")
    fun getActivities(
        @PathVariable("athleteId") athleteId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String,
    ): List<IntervalsActivityDTO>

    @PostMapping("/api/v1/athlete/{athleteId}/activities?name={name}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createActivity(
        @PathVariable athleteId: String,
        @PathVariable name: String,
        @RequestPart("file") file: MultipartFile
    ): ActivityResponseDTO

    @PutMapping("/api/v1/activity/{idActivity}")
    fun updateActivity(
        @PathVariable idActivity: String,
        @RequestBody requestDTO: IntervalsActivityDTO
    ): IntervalsActivityDTO

}
