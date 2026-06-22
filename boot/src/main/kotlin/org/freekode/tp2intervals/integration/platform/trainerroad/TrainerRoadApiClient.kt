package org.freekode.tp2intervals.integration.platform.trainerroad

import org.freekode.tp2intervals.integration.platform.trainerroad.activity.dto.TrainerRoadActivityDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.TRFindWorkoutsResponseDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRWorkoutResponseDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TrainerRoadTimelineDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "TrainerRoadApiClient",
    url = "\${app.trainer-road.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TrainerRoadApiClientConfig::class]
)
interface TrainerRoadApiClient {
    @GetMapping(
        value = ["/app/api/react-calendar/{memberId}/timeline"],
        headers = ["trainerroad-jsonformat=camel-case", "tr-cache-control=use-cache"]
    )
    fun getTimeline(
        @PathVariable("memberId") memberId: Long,
        @RequestParam("start") startDate: String,
        @RequestParam("end") endDate: String,
    ): TrainerRoadTimelineDTO

    @GetMapping(
        value = ["/app/api/react-calendar/{memberId}/activities"],
        headers = ["trainerroad-jsonformat=camel-case", "tr-cache-control=use-cache"]
    )
    fun getActivities(
        @PathVariable("memberId") memberId: Long,
        @RequestHeader("ids") ids: String,
    ): List<TrainerRoadActivityDTO>

    @GetMapping("/app/api/workouts")
    fun findWorkouts(
        @RequestBody requestDTO: TRFindWorkoutsRequestDTO,
    ): TRFindWorkoutsResponseDTO

    @GetMapping("/app/api/workoutdetails/{workoutId}")
    fun getWorkout(
        @PathVariable workoutId: String,
    ): TRWorkoutResponseDTO

    @PostMapping("/app/api/activities/{activityId}/exports/fit")
    fun exportFit(@PathVariable activityId: String): Resource
}