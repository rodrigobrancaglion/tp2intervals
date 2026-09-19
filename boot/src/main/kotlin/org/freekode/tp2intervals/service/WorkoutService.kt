package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
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
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class WorkoutService(
    workoutRepositories: List<IWorkoutRepository>,
    planRepositories: List<ILibraryContainerRepository>,
) {
    private val workoutRepositoryMap = workoutRepositories.associateBy { it.platform() }
    private val planRepositoryMap = planRepositories.associateBy { it.platform() }

    @LogService
    fun copyWorkoutsC2C(request: CopyC2CRequest): CopyWorkoutsResponse {
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
            ExternalData.empty()
        )
        filteredWorkoutsToSync.takeIf { it.isNotEmpty() }?.let { workouts ->
            targetWorkoutRepository.saveWorkoutsToCalendar(workouts)
        }
        return response
    }

    @LogService
    fun copyWorkoutsC2L(request: CopyC2LRequest): CopyWorkoutsResponse {
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

    @LogService
    fun copyWorkoutL2L(request: CopyL2LRequest): CopyWorkoutsResponse {
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!

        val workout = sourceWorkoutRepository.getWorkoutFromLibrary(request.workoutExternalData)
        targetWorkoutRepository.saveWorkoutsToLibrary(request.targetLibraryContainer, listOf(workout))
        return CopyWorkoutsResponse(1, 0, LocalDate.now(), LocalDate.now(), request.targetLibraryContainer.externalData)
    }

    @LogService
    fun findWorkoutsByName(platform: Platform, name: String): List<WorkoutDetails> {
        return workoutRepositoryMap[platform]!!.findWorkoutsFromLibraryByName(name)
    }

    @LogService
    fun deleteWorkoutsFromCalendar(request: DeleteWorkoutRequest) {
        val workoutRepository = workoutRepositoryMap[request.platform]!!
        workoutRepository.deleteWorkoutsFromCalendar(request.startDate, request.endDate)
    }
}