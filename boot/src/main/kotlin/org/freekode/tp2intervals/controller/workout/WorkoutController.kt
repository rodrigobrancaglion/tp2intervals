package org.freekode.tp2intervals.controller.workout

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.workout.*
import org.freekode.tp2intervals.service.WorkoutService
import org.springframework.web.bind.annotation.*

@RestController
class WorkoutController(
    private val workoutService: WorkoutService,
) {
    @PostMapping("/api/workout/copy-calendar-to-calendar")
    fun copyWorkoutsFromCalendarToCalendar(@RequestBody request: CopyC2CRequest): CopyWorkoutsResponse {
        return workoutService.copyWorkoutsC2C(request)
    }

    @PostMapping("/api/workout/copy-calendar-to-library")
    fun copyWorkoutsFromCalendarToLibrary(@RequestBody request: CopyC2LRequest): CopyWorkoutsResponse {
        return workoutService.copyWorkoutsC2L(request)
    }

    @PostMapping("/api/workout/copy-library-to-library")
    fun copyWorkoutFromLibraryToLibrary(@RequestBody request: CopyL2LRequest): CopyWorkoutsResponse {
        return workoutService.copyWorkoutL2L(request)
    }

    @GetMapping("/api/workout/find")
    fun findWorkoutsByName(@RequestParam platform: Platform, @RequestParam name: String): List<WorkoutDetailsResponse> {
        return workoutService.findWorkoutsByName(platform, name)
            .map { workoutDetails ->
                WorkoutDetailsResponse(
                    workoutDetails.name,
                    workoutDetails.duration.toString().replace("PT", "").lowercase(),
                    workoutDetails.tssPlanned,
                    workoutDetails.externalData
                )
            }
    }

    @DeleteMapping("/api/workout")
    fun deleteWorkoutsFromCalendar(@RequestBody request: DeleteWorkoutRequest) {
        workoutService.deleteWorkoutsFromCalendar(request)
    }
}
