package org.freekode.tp2intervals.integration.platform.rouvy

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.rouvy.activity.RouvyActivityRepository
import org.freekode.tp2intervals.integration.platform.rouvy.activity.RouvySyncManager
import org.freekode.tp2intervals.integration.platform.rouvy.configuration.RouvyConfigurationRepository
import org.freekode.tp2intervals.integration.platform.rouvy.configuration.dto.RouvyConfigurationDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.time.LocalDate

class RouvyActivityRepositoryTest {

    private val rouvySyncManager = mock(RouvySyncManager::class.java)
    private val rouvyActivityRepository = RouvyActivityRepository(rouvySyncManager)

    @Test
    fun `should propagate PlatformException when sync manager fails with timeout`() {
        val startDate = LocalDate.of(2026, 1, 1)
        val endDate = LocalDate.of(2026, 1, 7)
        val expectedMessage = "Batch extraction failed: Timeout 120000ms exceeded"

        `when`(rouvySyncManager.getActivitiesInDateRange(startDate, endDate))
            .thenThrow(PlatformException(Platform.ROUVY, expectedMessage))

        val exception = assertThrows(PlatformException::class.java) {
            rouvyActivityRepository.getActivities(startDate, endDate)
        }

        assertEquals(Platform.ROUVY, exception.platform)
        assertEquals(expectedMessage, exception.message)
    }

    @Test
    fun `should throw PlatformException when credentials are missing in sync manager`() {
        val ridersClient = mock(RouvyRidersClient::class.java)
        val configRepo = mock(RouvyConfigurationRepository::class.java)

        `when`(configRepo.getConfiguration()).thenReturn(RouvyConfigurationDTO("", ""))

        val syncManager = RouvySyncManager(
            ridersClient,
            configRepo,
            "https://account.rouvy.com",
            "https://riders.rouvy.com"
        )

        val exception = assertThrows(PlatformException::class.java) {
            syncManager.getActivitiesInDateRange(LocalDate.now(), LocalDate.now())
        }

        assertEquals(Platform.ROUVY, exception.platform)
        assertEquals("Rouvy credentials missing", exception.message)
    }

    @Test
    fun `should extract clean error message from Playwright exception string`() {
        val playwrightError = """
            Error {
              message='net::ERR_ABORTED at https://riders.rouvy.com/profile/overview
              name='Error
              stack='Error: net::ERR_ABORTED at https://riders.rouvy.com/profile/overview
                at FrameSession._navigate (crPage.js:512:35)
            }
        """.trimIndent()

        val extracted = RouvySyncManager.extractCleanErrorMessage(playwrightError)
        assertEquals("net::ERR_ABORTED at https://riders.rouvy.com/profile/overview", extracted)
    }

    @Test
    fun `should extract clean error message from plain exception string`() {
        val message = "Timeout 120000ms exceeded"
        val extracted = RouvySyncManager.extractCleanErrorMessage(message)
        assertEquals("Timeout 120000ms exceeded", extracted)
    }
}
