package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.activity.CopyActivitiesResponse
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.stereotype.Service

@Service
class ActivityService(
    repositories: List<IActivityRepository>
) {
    private val logger = AppLogger.get(this.javaClass)
    private val repositoryMap = repositories.associateBy { it.platform() }

    @LogService
    fun syncActivities(request: CopyC2CRequest): CopyActivitiesResponse {
        val targetPlatforms = request.allTargetPlatforms()
        if (targetPlatforms.isEmpty()) {
            throw IllegalArgumentException("At least one target platform must be specified")
        }

        val sourceActivityRepository = getRepository(request.sourcePlatform)
        val sourceActivities = sourceActivityRepository.getActivities(request.startDate, request.endDate)
        val activitiesToSave = sourceActivities.mapNotNull { it.filterActivity(request.types) }

        for (targetPlatform in targetPlatforms) {
            try {
                val targetActivityRepository = getRepository(targetPlatform)
                targetActivityRepository.saveActivities(activitiesToSave, request.types)
            } catch (e: Exception) {
                logger.errorL2In("Error saving activities to ${targetPlatform.title}: ${e.message}", e)
            }
        }

        return CopyActivitiesResponse(activitiesToSave.size, sourceActivities.size, request.startDate, request.endDate)
    }

    private fun getRepository(platform: Platform): IActivityRepository {
        return repositoryMap[platform] ?: throw IllegalArgumentException("Activity repository for platform ${platform.title} not found")
    }
}