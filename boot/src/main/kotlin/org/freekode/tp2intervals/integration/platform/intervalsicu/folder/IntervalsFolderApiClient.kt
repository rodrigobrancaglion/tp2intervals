package org.freekode.tp2intervals.integration.platform.intervalsicu.folder

import org.freekode.tp2intervals.integration.platform.intervalsicu.IntervalsApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.CreateFolderRequestDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.FolderDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "IntervalsFolderApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IntervalsApiClientConfig::class]
)
interface IntervalsFolderApiClient {

    @PostMapping("/api/v1/athlete/{athleteId}/folders")
    fun createFolder(
        @PathVariable("athleteId") athleteId: String,
        @RequestBody createFolderRequestDTO: CreateFolderRequestDTO
    ): FolderDTO

    @GetMapping("/api/v1/athlete/{athleteId}/folders")
    fun getFolders(
        @PathVariable("athleteId") athleteId: String,
    ): List<FolderDTO>

    @DeleteMapping("/api/v1/athlete/{athleteId}/folders/{folderId}")
    fun deleteFolder(
        @PathVariable("athleteId") athleteId: String,
        @PathVariable("folderId") folderId: String
    )
}