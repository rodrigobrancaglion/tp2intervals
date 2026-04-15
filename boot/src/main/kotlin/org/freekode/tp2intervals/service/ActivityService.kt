package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.activity.CopyActivitiesResponse
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.stereotype.Service

@Service
class ActivityService(
    repositories: List<IActivityRepository>
) {
    private val repositoryMap = repositories.associateBy { it.platform() }

    @LogService
    fun syncActivities(request: CopyC2CRequest): CopyActivitiesResponse {
        val sourceActivityRepository = getRepository(request.sourcePlatform)
        val targetActivityRepository = getRepository(request.targetPlatform)

        val sourceActivities = sourceActivityRepository.getActivities(request.startDate, request.endDate)
        val activitiesToSave = sourceActivities.mapNotNull { it.filterActivity(request.types) }

        targetActivityRepository.saveActivities(activitiesToSave, request.types)

        return CopyActivitiesResponse(activitiesToSave.size, sourceActivities.size, request.startDate, request.endDate)
    }

    private fun getRepository(platform: Platform) = repositoryMap[platform]!!
}