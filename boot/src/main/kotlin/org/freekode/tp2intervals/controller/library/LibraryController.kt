package org.freekode.tp2intervals.controller.library

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.dto.plan.CopyLibraryRequest
import org.freekode.tp2intervals.dto.plan.CopyPlanResponse
import org.freekode.tp2intervals.service.LibraryService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
class LibraryController(
    private val libraryService: LibraryService
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/api/library-container")
    fun getLibraryContainers(@RequestParam platform: Platform): List<LibraryContainer> {
        log.debug("Received request for getting library containers: {}", platform)
        return libraryService.findByPlatform(platform)
    }

    @PostMapping("/api/library-container/copy")
    fun copyLibraryContainer(@RequestBody request: CopyLibraryRequest): CopyPlanResponse {
        log.debug("Received request to copy the library container: {}", request)
        return libraryService.copyLibrary(request)
    }
}
