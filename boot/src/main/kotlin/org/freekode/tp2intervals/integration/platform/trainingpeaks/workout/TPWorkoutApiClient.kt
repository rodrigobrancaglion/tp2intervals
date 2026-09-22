package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPApiClientConfig
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.*
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "TrainingPeaksWorkoutApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TPApiClientConfig::class]
)
interface TPWorkoutApiClient {
    @GetMapping("/fitness/v6/athletes/{userId}/workouts/{startDate}/{endDate}")
    fun getWorkouts(
        @PathVariable("userId") userId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String
    ): List<TPWorkoutCalendarDTO>

    @GetMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}")
    fun getWorkout(
        @PathVariable("userId") userId: String,
        @PathVariable("workoutId") workoutId: Long,
    ): TPWorkoutCalendarDTO


    @GetMapping("/fitness/v1/athletes/{userId}/calendarNote/{startDate}/{endDate}")
    fun getNotes(
        @PathVariable("userId") userId: String,
        @PathVariable("startDate") startDate: String,
        @PathVariable("endDate") endDate: String
    ): List<TPNoteResponseDTO>

    @GetMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}/fordevice/fit")
    fun downloadWorkoutFit(
        @PathVariable("userId") userId: String,
        @PathVariable("workoutId") workoutId: String,
    ): Resource

    @GetMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}/details")
    fun getWorkoutDetails(
        @PathVariable userId: String,
        @PathVariable workoutId: Long,
    ): TPWorkoutDetailsResponseDTO

    @GetMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}/rawfiledata/{attachmentId}")
    fun downloadWorkoutAttachment(
        @PathVariable userId: String,
        @PathVariable workoutId: Long,
        @PathVariable attachmentId: String,
    ): Resource

    @PostMapping("/fitness/v6/athletes/{userId}/workouts")
    fun createAndPlanWorkout(
        @PathVariable("userId") userId: String,
        @RequestBody requestDTO: CreateTPWorkoutRequestDTO
    )

    @DeleteMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}")
    fun deleteWorkout(
        @PathVariable userId: String,
        @PathVariable workoutId: String,
    ): Boolean

    @PostMapping("/fitness/v6/athletes/{userId}/workouts/{workoutId}/comments")
    fun createComment(
        @PathVariable("userId") userId: String,
        @PathVariable("workoutId") workoutId: String,
        @RequestBody requestDTO: TPWorkoutCommentRequestDTO
    ): TPWorkoutCommentDTO
}