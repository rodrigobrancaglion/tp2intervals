package org.freekode.tp2intervals.service

import org.freekode.tp2intervals.aspect.LogService
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.structure.StepModifier
import org.freekode.tp2intervals.dto.plan.CopyLibraryRequest
import org.freekode.tp2intervals.dto.plan.CopyPlanResponse
import org.freekode.tp2intervals.dto.plan.CreateLibraryContainerRequest
import org.freekode.tp2intervals.dto.plan.DeleteLibraryRequest
import org.freekode.tp2intervals.integration.provider.librarycontainer.ILibraryContainerRepository
import org.freekode.tp2intervals.integration.provider.workout.IWorkoutRepository
import org.springframework.stereotype.Service

@Service
class LibraryService(
    workoutRepositories: List<IWorkoutRepository>,
    planRepositories: List<ILibraryContainerRepository>,
) {

    private val workoutRepositoryMap = workoutRepositories.associateBy { it.platform() }
    private val planRepositoryMap = planRepositories.associateBy { it.platform() }

    fun findByPlatform(platform: Platform): List<LibraryContainer> {
        val repository = planRepositoryMap[platform]!!
        return repository.getLibraryContainers()
    }

    @LogService
    fun copyLibrary(request: CopyLibraryRequest): CopyPlanResponse {
        val targetPlanRepository = planRepositoryMap[request.targetPlatform]!!
        val sourceWorkoutRepository = workoutRepositoryMap[request.sourcePlatform]!!
        val targetWorkoutRepository = workoutRepositoryMap[request.targetPlatform]!!

        val workouts = sourceWorkoutRepository.getWorkoutsFromLibrary(request.libraryContainer)
            .map { it.addWorkoutStepModifier(request.stepModifier) }
        val newPlan = targetPlanRepository.createLibraryContainer(
            request.newName,
            request.libraryContainer.isPlan,
            workouts.first().date?.toLocalDate()
        )
        targetWorkoutRepository.saveWorkoutsToLibrary(newPlan, workouts)
        return CopyPlanResponse(newPlan.name, workouts.size, newPlan.externalData)
    }

    @LogService
    fun deleteLibrary(request: DeleteLibraryRequest) {
        val planRepository = planRepositoryMap[request.platform]!!
        planRepository.deleteLibraryContainer(request.externalData)
    }

    @LogService
    fun create(request: CreateLibraryContainerRequest): LibraryContainer {
        val planRepository = planRepositoryMap[request.platform]!!
        return planRepository.createLibraryContainer(request.name, false, null)
    }

    @LogService
    private fun Workout.addWorkoutStepModifier(stepModifier: StepModifier): Workout =
        Workout(details, date, structure?.addModifier(stepModifier))
}