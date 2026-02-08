package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.dto.CopyC2CRequest
import org.freekode.tp2intervals.dto.workout.CopyC2LRequest
import org.freekode.tp2intervals.dto.workout.CopyL2LRequest
import org.freekode.tp2intervals.dto.workout.CopyWorkoutsResponse
import org.freekode.tp2intervals.dto.workout.DeleteWorkoutRequest
import org.freekode.tp2intervals.integration.provider.librarycontainer.ILibraryContainerRepository
import org.freekode.tp2intervals.integration.provider.workout.IWorkoutRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WorkoutService(
    workoutRepositories: List<IWorkoutRepository>,
    planRepositories: List<ILibraryContainerRepository>,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val workoutRepositoryMap = workoutRepositories.associateBy { it.platform() }
    private val planRepositoryMap = planRepositories.associateBy { it.platform() }

    fun copyWorkoutsC2C(request: CopyC2CRequest): CopyWorkoutsResponse {
        log.info("Received request for copy calendar to calendar: $request")
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!

        val allWorkoutsToSync = sourceWorkoutRepository.getWorkoutsFromCalendar(request.startDate, request.endDate)
        var filteredWorkoutsToSync = allWorkoutsToSync.filter { workout ->
            workout.details.type.matchesAny(request.types)
        }
        if (request.skipSynced) {
            val plannedWorkouts = targetWorkoutRepository.getWorkoutsFromCalendar(request.startDate, request.endDate)
            filteredWorkoutsToSync = filteredWorkoutsToSync.filter { !plannedWorkouts.contains(it) }
        }

        val response = CopyWorkoutsResponse(
            filteredWorkoutsToSync.size,
            allWorkoutsToSync.size - filteredWorkoutsToSync.size,
            request.startDate,
            request.endDate,
            ExternalData.empty() // TODO figure smth better
        )
        targetWorkoutRepository.saveWorkoutsToCalendar(filteredWorkoutsToSync)
        log.info("Saved workouts to calendar successfully: $response")
        return response
    }

    fun copyWorkoutsC2L(request: CopyC2LRequest): CopyWorkoutsResponse {
        log.info("Received request for copy calendar to library: $request")
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!
        val targetPlanRepository = planRepositoryMap[request.targetPlatform]!!

        val allWorkouts = sourceWorkoutRepository.getWorkoutsFromCalendar(request.startDate, request.endDate)
        val filteredWorkouts = allWorkouts.filter { request.types.contains(it.details.type) }

        val newPlan = targetPlanRepository.createLibraryContainer(request.name, request.isPlan, request.startDate)
        targetWorkoutRepository.saveWorkoutsToLibrary(newPlan, filteredWorkouts)
        return CopyWorkoutsResponse(
            filteredWorkouts.size,
            allWorkouts.size - filteredWorkouts.size,
            request.startDate,
            request.endDate,
            newPlan.externalData
        )
    }

    fun copyWorkoutL2L(request: CopyL2LRequest): CopyWorkoutsResponse {
        log.info("Received request for copy library to library: $request")
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!

        val workout = sourceWorkoutRepository.getWorkoutFromLibrary(request.workoutExternalData)
        targetWorkoutRepository.saveWorkoutsToLibrary(request.targetLibraryContainer, listOf(workout))
        return CopyWorkoutsResponse(1, 0, LocalDate.now(), LocalDate.now(), request.targetLibraryContainer.externalData)
    }

    fun findWorkoutsByName(platform: Platform, name: String): List<WorkoutDetails> {
        log.info("Received request for find workouts by name, platform: $platform, name: $name")
        return workoutRepositoryMap[platform]!!.findWorkoutsFromLibraryByName(name)
    }

    fun deleteWorkoutsFromCalendar(request: DeleteWorkoutRequest) {
        log.info("Received request to delete workouts from calendar: $request")
        val workoutRepository = workoutRepositoryMap[request.platform]!!
        workoutRepository.deleteWorkoutsFromCalendar(request.startDate, request.endDate)
    }
}