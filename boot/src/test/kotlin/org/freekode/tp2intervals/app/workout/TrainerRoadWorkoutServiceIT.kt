package org.freekode.tp2intervals.dto.workout

import config.BaseSpringITConfig
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.TrainingType
import org.freekode.tp2intervals.dto.plan.CreateLibraryContainerRequest
import org.freekode.tp2intervals.dto.plan.DeleteLibraryRequest
import org.freekode.tp2intervals.service.LibraryService
import org.freekode.tp2intervals.service.WorkoutService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import java.time.LocalDate
import java.time.LocalDateTime

@EnableWireMock(ConfigureWireMock(port = 34567))
class TrainerRoadWorkoutServiceIT : BaseSpringITConfig() {
    @Autowired
    lateinit var libraryService: LibraryService

    @Autowired
    lateinit var workoutService: WorkoutService

    private val platform = Platform.TRAINER_ROAD

    //@Test
    fun `should copy workouts from library to library`() {
        val foundWorkouts = workoutService.findWorkoutsByName(platform, "complex")

        val libraryContainer = libraryService.create(
            CreateLibraryContainerRequest("copy form lib to lib ${LocalDateTime.now()}", Platform.INTERVALS)
        )

        val copyRequest = CopyL2LRequest(
            foundWorkouts.first().externalData,
            libraryContainer,
            platform,
            Platform.INTERVALS
        )
        val response = workoutService.copyWorkoutL2L(copyRequest)
        libraryService.deleteLibrary(DeleteLibraryRequest(libraryContainer.externalData, Platform.INTERVALS))
        assertEquals(response.copied, 1)
    }

    @Test
    @Disabled("don't have example response for calendar")
    fun `should copy planned workouts to library`() {
        val response = workoutService.copyWorkoutsC2L(
            CopyC2LRequest(
                LocalDate.parse("2024-03-04"),
                LocalDate.parse("2024-03-10"),
                "copy from calend to lib ${LocalDateTime.now()}",
                true,
                TrainingType.DEFAULT_LIST,
                platform,
                Platform.INTERVALS
            )
        )
        libraryService.deleteLibrary(DeleteLibraryRequest(response.externalData, Platform.INTERVALS))

        Assertions.assertEquals(response.copied, 5)
    }
}
