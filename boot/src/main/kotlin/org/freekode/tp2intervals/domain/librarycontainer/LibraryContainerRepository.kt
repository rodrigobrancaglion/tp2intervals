package org.freekode.tp2intervals.domain.librarycontainer

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import java.time.LocalDate

interface LibraryContainerRepository {
    fun platform(): Platform

    fun createLibraryContainer(name: String, isPlan: Boolean, startDate: LocalDate?): LibraryContainer

    fun getLibraryContainers(): List<LibraryContainer>

    fun deleteLibraryContainer(externalData: ExternalData)
}
