package org.freekode.tp2intervals.integration.platform.intervalsicu.folder

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.IcuCreateFolderDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.folder.dto.IcuFolder
import org.freekode.tp2intervals.integration.provider.librarycontainer.ILibraryContainerRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.LocalDate


@CacheConfig(cacheNames = ["libraryItemsCache"])
@Repository
class IcuFolderContainerRepository(
    private val icuFolderApiClient: IcuFolderApiClient,
    private val icuConfigurationRepository: IcuConfigurationRepository,
) : ILibraryContainerRepository {

    override fun platform() = Platform.INTERVALS

    override fun createLibraryContainer(name: String, isPlan: Boolean, startDate: LocalDate?): LibraryContainer {
        val folderType = if (isPlan) "PLAN" else "FOLDER"
        val newFolder = createFolder(name, startDate, folderType)
        return toPlan(newFolder)
    }

    @Cacheable(key = "'intervals'")
    override fun getLibraryContainers(): List<LibraryContainer> {
        return icuFolderApiClient.getFolders(icuConfigurationRepository.getConfiguration().athleteId)
            .map { toPlan(it) }
    }

    override fun deleteLibraryContainer(externalData: ExternalData) {
        icuFolderApiClient.deleteFolder(
            icuConfigurationRepository.getConfiguration().athleteId,
            externalData.intervalsId!!
        )
    }

    private fun createFolder(name: String, startDate: LocalDate?, type: String): IcuFolder {
        val createRequest = IcuCreateFolderDTO(
            0, name, "",//Signature.description,
            0, startDate?.toString(), -1, -1, type
        )
        return icuFolderApiClient.createFolder(
            icuConfigurationRepository.getConfiguration().athleteId,
            createRequest
        )
    }

    private fun toPlan(icuFolder: IcuFolder): LibraryContainer {
        return if (icuFolder.type == "PLAN") {
            LibraryContainer(
                icuFolder.name,
                icuFolder.startDateLocal!!,
                true,
                icuFolder.num_workouts,
                ExternalData.empty().withIntervals(icuFolder.id)
            )
        } else {
            LibraryContainer.planFromMonday(
                icuFolder.name,
                icuFolder.num_workouts,
                ExternalData.empty().withIntervals(icuFolder.id)
            )
        }
    }
}
