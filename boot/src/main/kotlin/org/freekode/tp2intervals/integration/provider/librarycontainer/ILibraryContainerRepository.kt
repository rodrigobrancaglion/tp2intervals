package org.freekode.tp2intervals.integration.provider.librarycontainer

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import java.time.LocalDate

interface ILibraryContainerRepository {
    fun platform(): Platform

    fun createLibraryContainer(name: String, isPlan: Boolean, startDate: LocalDate?): LibraryContainer

    fun getLibraryContainers(): List<LibraryContainer>

    fun deleteLibraryContainer(externalData: ExternalData)
}