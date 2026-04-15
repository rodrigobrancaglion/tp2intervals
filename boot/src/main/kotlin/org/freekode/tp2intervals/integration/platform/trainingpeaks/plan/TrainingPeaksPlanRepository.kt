package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.trainingpeaks.library.TPWorkoutLibraryRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.ApplyTPPlanRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.ApplyTPPlanResponseDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPPlanDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.provider.librarycontainer.ILibraryContainerRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["libraryItemsCache"])
@Repository
class TrainingPeaksPlanRepository(
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
    private val tpWorkoutLibraryRepository: TPWorkoutLibraryRepository,
    private val trainingPeaksPlanApiClient: TrainingPeaksPlanApiClient,
) : ILibraryContainerRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    override fun createLibraryContainer(name: String, isPlan: Boolean, startDate: LocalDate?): LibraryContainer {
        throw PlatformException(platform(), "Doesn't support plan creation")
    }

    @Cacheable(key = "'training-peaks'")
    override fun getLibraryContainers(): List<LibraryContainer> {
        val plans = trainingPeaksPlanApiClient.getPlans()
            .map { toLibraryContainer(it) }
            .sortedBy { it.name }
        val libraries = tpWorkoutLibraryRepository.getLibraries()
            .sortedBy { it.name }
        return (plans + libraries)
            .toList()
    }

    override fun deleteLibraryContainer(externalData: ExternalData) {
        TODO("Not yet implemented")
    }

    fun getPlan(planId: String): TPPlanDTO {
        return trainingPeaksPlanApiClient.getPlan(planId)
    }

    fun applyPlan(planId: String, startDate: LocalDate): ApplyTPPlanResponseDTO {
        val request = ApplyTPPlanRequestDTO(
            trainingPeaksUserRepository.getUser().userId,
            planId,
            startDate.toString(),
            "1"
        )
        return trainingPeaksPlanApiClient.applyPlan(listOf(request)).first()
    }

    fun removeAppliedPlan(appliedPlanId: String) {
        val request = mapOf(
            "appliedPlanId" to appliedPlanId
        )
        trainingPeaksPlanApiClient.removePlan(request)
    }

    private fun toLibraryContainer(planDto: TPPlanDTO): LibraryContainer {
        return LibraryContainer.planFromMonday(
            planDto.title,
            planDto.workoutCount,
            ExternalData.empty().withTrainingPeaks(planDto.planId)
        )
    }
}
