package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.event.CopyEventsResponse
import org.freekode.tp2intervals.integration.provider.event.IEventRepository
import org.springframework.stereotype.Service

@Service
class EventService(
    repositories: List<IEventRepository>
) {
    private val repositoryMap = repositories.associateBy { it.platform() }

    @LogService
    fun syncEvents(request: CopyC2CRequest): CopyEventsResponse {
        val sourceRepo = getRepository(request.sourcePlatform)
        val targetRepo = getRepository(request.targetPlatform)

        val events = sourceRepo.getEvents(request.startDate, request.endDate)
        targetRepo.saveEvents(events)

        return CopyEventsResponse(events.size, request.startDate, request.endDate)
    }

    private fun getRepository(platform: Platform) = repositoryMap[platform]!!
}
