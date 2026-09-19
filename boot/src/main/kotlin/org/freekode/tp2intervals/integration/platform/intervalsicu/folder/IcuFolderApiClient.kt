package org.freekode.tp2intervals.integration.platform.intervalsicu.folder

import org.freekode.tp2intervals.integration.platform.intervalsicu.IcuApiClientConfig
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.IcuCreateFolderDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.IcuFolder
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "IntervalsFolderApiClient",
    url = "\${app.intervals.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [IcuApiClientConfig::class]
)
interface IcuFolderApiClient {

    @PostMapping("/api/v1/athlete/{athleteId}/folders")
    fun createFolder(
        @PathVariable("athleteId") athleteId: String,
        @RequestBody icuCreateFolderDTO: IcuCreateFolderDTO
    ): IcuFolder

    @GetMapping("/api/v1/athlete/{athleteId}/folders")
    fun getFolders(
        @PathVariable("athleteId") athleteId: String,
    ): List<IcuFolder>

    @DeleteMapping("/api/v1/athlete/{athleteId}/folders/{folderId}")
    fun deleteFolder(
        @PathVariable("athleteId") athleteId: String,
        @PathVariable("folderId") folderId: String
    )
}