package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.wellness.CopyWellnessResponse
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.stereotype.Service

@Service
class WellnessService(
    repositories: List<IWellnessRepository>,
) {
    private val repositoryMap = repositories.associateBy { it.platform() }

    @LogService
    fun copyWellnessC2C(request: CopyC2CRequest): CopyWellnessResponse {
        val sourceRepository = repositoryMap[request.sourcePlatform]!!
        val targetRepository = repositoryMap[request.targetPlatform]!!

        val allToSync = sourceRepository.getFromCalendar(request.startDate, request.endDate)
            .map { it.filterWellness(request.types) }

        val response = CopyWellnessResponse(
            allToSync.size,
            request.startDate,
            request.endDate,
            ExternalData.empty()
        )
        targetRepository.saveToCalendar(allToSync, request.startDate, request.endDate)
        return response
    }

}