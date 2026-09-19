package org.freekode.tp2intervals.integration.platform.trainerroad

import org.freekode.tp2intervals.integration.platform.trainerroad.activity.dto.TrainerRoadActivityDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRFindWorkoutsResponseDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRTimelineDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRWorkoutResponseDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.*

@FeignClient(
    value = "TrainerRoadApiClient",
    url = "\${app.trainer-road.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TRApiClientConfig::class]
)
interface TRApiClient {
    @GetMapping(
        value = ["/app/api/react-calendar/{memberId}/timeline"],
        headers = ["trainerroad-jsonformat=camel-case", "tr-cache-control=use-cache"]
    )
    fun getTimeline(
        @PathVariable("memberId") memberId: Long,
        @RequestParam("start") startDate: String,
        @RequestParam("end") endDate: String,
    ): TRTimelineDTO

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
        @RequestParam("searchText") searchText: String,
        @RequestParam("pageNumber") pageNumber: Int,
        @RequestParam("pageSize") pageSize: Int,
    ): TRFindWorkoutsResponseDTO

    @GetMapping("/app/api/workoutdetails/{workoutId}")
    fun getWorkout(
        @PathVariable workoutId: String,
    ): TRWorkoutResponseDTO

    @PostMapping("/app/api/activities/{activityId}/exports/fit")
    fun exportFit(@PathVariable activityId: String): Resource
}