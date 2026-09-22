package org.freekode.tp2intervals.integration.platform.trainingpeaks.plan

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.trainingpeaks.library.TPWorkoutLibraryRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPApplyPlanRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPApplyPlanResponseDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.dto.TPPlanDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TPUserRepository
import org.freekode.tp2intervals.integration.provider.librarycontainer.ILibraryContainerRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.LocalDate

@CacheConfig(cacheNames = ["libraryItemsCache"])
@Repository
class TPPlanRepository(
    private val tpUserRepository: TPUserRepository,
    private val tpWorkoutLibraryRepository: TPWorkoutLibraryRepository,
    private val tpPlanApiClient: TPPlanApiClient,
) : ILibraryContainerRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    override fun createLibraryContainer(name: String, isPlan: Boolean, startDate: LocalDate?): LibraryContainer {
        throw PlatformException(platform(), "Doesn't support plan creation")
    }

    @Cacheable(key = "'training-peaks'")
    override fun getLibraryContainers(): List<LibraryContainer> {
        val plans = tpPlanApiClient.getPlans()
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
        return tpPlanApiClient.getPlan(planId)
    }

    fun applyPlan(planId: String, startDate: LocalDate): TPApplyPlanResponseDTO {
        val request = TPApplyPlanRequestDTO(
            tpUserRepository.getUser().userId,
            planId,
            startDate.toString(),
            "1"
        )
        return tpPlanApiClient.applyPlan(listOf(request)).first()
    }

    fun removeAppliedPlan(appliedPlanId: String) {
        val request = mapOf(
            "appliedPlanId" to appliedPlanId
        )
        tpPlanApiClient.removePlan(request)
    }

    private fun toLibraryContainer(planDto: TPPlanDTO): LibraryContainer {
        return LibraryContainer.planFromMonday(
            planDto.title,
            planDto.workoutCount,
            ExternalData.empty().withTrainingPeaks(planDto.planId)
        )
    }
}
