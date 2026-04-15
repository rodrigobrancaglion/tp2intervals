package org.freekode.tp2intervals.integration.platform.strava.activity

import org.freekode.tp2intervals.integration.platform.strava.activity.dto.StravaActivityUploadResponseDTO
import org.freekode.tp2intervals.integration.platform.strava.activity.dto.StravaActivityUploadStatusResponseDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

/**
 * Feign client for uploading activity files to Strava and polling upload status.
 */
@FeignClient(
    value = "StravaActivityUploadClient",
    url = "\${app.strava.api-url}/api/v3",
    configuration = [StravaActivityClientConfig::class]
)
interface StravaActivityUploadClient {

    @PostMapping("/uploads", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createUpload(
        @RequestPart("file") file: MultipartFile,
        @RequestPart("data_type") dataType: String,
        @RequestPart("name") name: String,
        @RequestPart("description") description: String?,
        @RequestPart("external_id") externalId: String?,
    ): StravaActivityUploadResponseDTO

    @GetMapping("/uploads/{uploadId}")
    fun getUploadStatus(@PathVariable uploadId: Long): StravaActivityUploadStatusResponseDTO
}
