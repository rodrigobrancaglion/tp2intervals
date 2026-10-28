package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.domain.ActivityType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.time.LocalDate
import java.time.LocalDateTime

class ActivityServiceTest {

    private lateinit var rouvyRepo: IActivityRepository
    private lateinit var tpRepo: IActivityRepository
    private lateinit var stravaRepo: IActivityRepository
    private lateinit var icuRepo: IActivityRepository
    private lateinit var activityService: ActivityService

    private val startDate = LocalDate.of(2026, 1, 1)
    private val endDate = LocalDate.of(2026, 1, 7)

    @BeforeEach
    fun setUp() {
        rouvyRepo = mock(IActivityRepository::class.java).apply {
            `when`(platform()).thenReturn(Platform.ROUVY)
        }
        tpRepo = mock(IActivityRepository::class.java).apply {
            `when`(platform()).thenReturn(Platform.TRAINING_PEAKS)
        }
        stravaRepo = mock(IActivityRepository::class.java).apply {
            `when`(platform()).thenReturn(Platform.STRAVA)
        }
        icuRepo = mock(IActivityRepository::class.java).apply {
            `when`(platform()).thenReturn(Platform.INTERVALS)
        }

        activityService = ActivityService(listOf(rouvyRepo, tpRepo, stravaRepo, icuRepo))
    }

    @Test
    fun `should sync activities to multiple target platforms fetching from source only once`() {
        val testActivity = Activity(
            workoutId = 12345L,
            startedAt = LocalDateTime.now(),
            type = null,
            title = "Rouvy Ride",
            description = "Test Description",
            resource = "mockFitBase64",
            fileName = "12345.fit",
            rpe = null,
            feel = null
        )

        `when`(rouvyRepo.getActivities(startDate, endDate)).thenReturn(listOf(testActivity))

        val request = CopyC2CRequest(
            startDate = startDate,
            endDate = endDate,
            types = listOf(ActivityType.ACTIVITY),
            skipSynced = false,
            sourcePlatform = Platform.ROUVY,
            targetPlatforms = listOf(Platform.TRAINING_PEAKS, Platform.STRAVA, Platform.INTERVALS)
        )

        val response = activityService.syncActivities(request)

        assertEquals(1, response.copied)
        assertEquals(1, response.filteredOut)
        assertEquals(startDate, response.startDate)
        assertEquals(endDate, response.endDate)

        // Source must be queried exactly once
        verify(rouvyRepo, times(1)).getActivities(startDate, endDate)

        // Each target repository must have received the save call
        verify(tpRepo, times(1)).saveActivities(listOf(testActivity), listOf(ActivityType.ACTIVITY))
        verify(stravaRepo, times(1)).saveActivities(listOf(testActivity), listOf(ActivityType.ACTIVITY))
        verify(icuRepo, times(1)).saveActivities(listOf(testActivity), listOf(ActivityType.ACTIVITY))
    }

    @Test
    fun `should support backward compatibility with single targetPlatform`() {
        val testActivity = Activity(
            workoutId = 12345L,
            startedAt = LocalDateTime.now(),
            type = null,
            title = "Rouvy Ride",
            description = null,
            resource = "mockFitBase64",
            fileName = "12345.fit",
            rpe = null,
            feel = null
        )

        `when`(rouvyRepo.getActivities(startDate, endDate)).thenReturn(listOf(testActivity))

        val request = CopyC2CRequest(
            startDate = startDate,
            endDate = endDate,
            types = listOf(ActivityType.ACTIVITY),
            skipSynced = false,
            sourcePlatform = Platform.ROUVY,
            targetPlatform = Platform.STRAVA
        )

        val response = activityService.syncActivities(request)

        assertEquals(1, response.copied)
        verify(rouvyRepo, times(1)).getActivities(startDate, endDate)
        verify(stravaRepo, times(1)).saveActivities(listOf(testActivity), listOf(ActivityType.ACTIVITY))
        verify(tpRepo, never()).saveActivities(anyList(), anyList())
        verify(icuRepo, never()).saveActivities(anyList(), anyList())
    }

    @Test
    fun `should throw exception if no target platforms provided`() {
        val request = CopyC2CRequest(
            startDate = startDate,
            endDate = endDate,
            types = listOf(ActivityType.ACTIVITY),
            skipSynced = false,
            sourcePlatform = Platform.ROUVY
        )

        assertThrows(IllegalArgumentException::class.java) {
            activityService.syncActivities(request)
        }
    }
}
