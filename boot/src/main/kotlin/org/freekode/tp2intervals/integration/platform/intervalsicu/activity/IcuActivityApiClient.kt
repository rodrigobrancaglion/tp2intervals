package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.freekode.tp2intervals.integration.platform.intervalsicu.IcuApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto.IcuActivity
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto.IcuActivityResponse
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto.IcuNewActivityMsg
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@FeignClient(
    value = "IntervalsActivityApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IcuApiClientConfig::class]
)
interface IcuActivityApiClient {

    @GetMapping("/api/v1/athlete/{id}/activities?oldest={startDate}&newest={endDate}")
    fun getActivities(
        @PathVariable("id") athleteId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String,
    ): List<IcuActivity>

    @PostMapping("/api/v1/athlete/{id}/activities?name={name}&description{description}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createActivity(
        @PathVariable id: String,
        @PathVariable name: String,
        @PathVariable description: String,
        @RequestPart("file") file: MultipartFile
    ): IcuActivityResponse

    @PutMapping("/api/v1/activity/{id}")
    fun updateActivity(
        @PathVariable id: String,
        @RequestBody requestDTO: IcuActivity
    ): String

    @PostMapping("/api/v1/activity/{id}/messages", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveComment(
        @PathVariable("id") id: String,
        @RequestBody content: IcuNewActivityMsg
    ): String
}
