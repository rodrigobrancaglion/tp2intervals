package org.freekode.tp2intervals.infrastructure.intervalsicu.folder

import java.time.LocalDate
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.plan.Plan
import org.freekode.tp2intervals.domain.plan.PlanId
import org.freekode.tp2intervals.domain.plan.PlanRepository
import org.freekode.tp2intervals.domain.plan.PlanType
import org.freekode.tp2intervals.infrastructure.intervalsicu.IntervalsApiClient
import org.freekode.tp2intervals.infrastructure.intervalsicu.configuration.IntervalsConfigurationRepository
import org.springframework.stereotype.Repository


@Repository
class IntervalsFolderRepository(
    private val intervalsApiClient: IntervalsApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository
) : PlanRepository {

    override fun createPlan(name: String, startDate: LocalDate, type: PlanType): Plan {
        val folderType = if (type == PlanType.PLAN) "PLAN" else "FOLDER"
        val newFolder = createFolder(name, startDate, folderType)
        return Plan(PlanId(newFolder.id), newFolder.startDateLocal!!)
    }

    override fun platform() = Platform.INTERVALS

    private fun createFolder(name: String, startDate: LocalDate?, type: String): FolderDTO {
        val createRequest = CreateFolderRequestDTO(
            0, name, 0, startDate?.toString(), -1, -1, type
        )
        return intervalsApiClient.createFolder(
            intervalsConfigurationRepository.getConfiguration().athleteId,
            createRequest
        )
    }
}
