package org.freekode.tp2intervals.controller.library

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.dto.plan.CopyLibraryRequest
import org.freekode.tp2intervals.dto.plan.CopyPlanResponse
import org.freekode.tp2intervals.service.LibraryService
import org.springframework.web.bind.annotation.*

@RestController
class LibraryController(
    private val libraryService: LibraryService
) {
     private val logger = AppLogger.get(this.javaClass)

    @GetMapping("/api/library-container")
    fun getLibraryContainers(@RequestParam platform: Platform): List<LibraryContainer> {
        logger.debugL3In("Received request for getting library containers: {}", platform)
        return libraryService.findByPlatform(platform)
    }

    @PostMapping("/api/library-container/copy")
    fun copyLibraryContainer(@RequestBody request: CopyLibraryRequest): CopyPlanResponse {
        logger.debugL3In("Received request to copy the library container: {}", request)
        return libraryService.copyLibrary(request)
    }
}
