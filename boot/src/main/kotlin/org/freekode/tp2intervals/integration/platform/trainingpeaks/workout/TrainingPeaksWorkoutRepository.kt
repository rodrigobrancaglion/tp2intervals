package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.aspect.LogRepository
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.trainingpeaks.configuration.TrainingPeaksConfigurationRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.library.TPWorkoutLibraryRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.TrainingPeaksPlanCoachApiClient
import org.freekode.tp2intervals.integration.platform.trainingpeaks.plan.TrainingPeaksPlanRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.CreateTPWorkoutRequestDTO
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.structure.ToTPStructureConverter
import org.freekode.tp2intervals.integration.provider.workout.IWorkoutRepository
import org.freekode.tp2intervals.integration.utils.Date
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.LocalDate


@CacheConfig(cacheNames = ["tpWorkoutsCache"])
@Repository
class TrainingPeaksWorkoutRepository(
    private val trainingPeaksWorkoutApiClient: TrainingPeaksWorkoutApiClient,
    private val trainingPeaksPlanCoachApiClient: TrainingPeaksPlanCoachApiClient,
    private val tpToWorkoutConverter: TPToWorkoutConverter,
    private val trainingPeaksPlanRepository: TrainingPeaksPlanRepository,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository,
    private val tpWorkoutLibraryRepository: TPWorkoutLibraryRepository,
    private val tpAttachmentService: TPAttachmentService,
    private val trainingPeaksConfigurationRepository: TrainingPeaksConfigurationRepository,
    private val objectMapper: ObjectMapper,
) : IWorkoutRepository {

    override fun platform() = Platform.TRAINING_PEAKS

    @LogRepository
    override fun saveWorkoutsToCalendar(workouts: List<Workout>) {
        workouts.forEach { saveWorkoutToCalendar(it) }
    }

    @LogRepository
    @Cacheable
    override fun getWorkoutsFromLibrary(libraryContainer: LibraryContainer): List<Workout> {
        val user = trainingPeaksUserRepository.getUser()
        return if (libraryContainer.isPlan) {
            if (user.isAthlete) {
                getWorkoutsFromTPPlan(libraryContainer)
            } else {
                getWorkoutsFromTPCoachPlan(libraryContainer)
            }
        } else {
            getWorkoutsFromTPLibrary(libraryContainer)
        }
    }

    private fun getWorkoutsFromTPCoachPlan(libraryContainer: LibraryContainer): List<Workout> {
        val planWorkouts = trainingPeaksPlanCoachApiClient.getPlanWorkouts(
            libraryContainer.externalData.trainingPeaksId!!,
            libraryContainer.startDate.minusYears(10).toString(),
            libraryContainer.startDate.plusYears(2).toString()
        )
        val planNotes = trainingPeaksPlanCoachApiClient.getPlanNotes(
            libraryContainer.externalData.trainingPeaksId,
            libraryContainer.startDate.minusYears(10).toString(),
            libraryContainer.startDate.plusYears(2).toString()
        )

        val workouts = planWorkouts.map {
            tpToWorkoutConverter.toWorkout(it)
        }

        val notes = planNotes.map { tpToWorkoutConverter.toWorkout(it) }
        return workouts + notes
    }

    @LogRepository
    override fun getWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Workout> {
        val userId = trainingPeaksUserRepository.getUser().userId
        val tpWorkouts = trainingPeaksWorkoutApiClient.getWorkouts(userId, startDate.toString(), endDate.toString())

        val noteEndDate = getNoteEndDateForFilter(startDate, endDate)
        val tpNotes = trainingPeaksWorkoutApiClient.getNotes(userId, startDate.toString(), noteEndDate.toString())
        val workouts = tpWorkouts
            .filter { it.isValidForImport() }
            .map {
                val attachments = tpAttachmentService.getAttachments(userId, it.workoutId)
                tpToWorkoutConverter.toWorkout(it, attachments)
            }

        val notes = tpNotes.map { tpToWorkoutConverter.toWorkout(it) }
        return workouts + notes
    }

    @LogRepository
    override fun findWorkoutsFromLibraryByName(name: String): List<WorkoutDetails> {
        return tpWorkoutLibraryRepository.getAllWorkouts()
            .map { it.details }
            .filter { it.name.contains(name) }
    }

    @LogRepository
    override fun getWorkoutFromLibrary(externalData: ExternalData): Workout {
        return tpWorkoutLibraryRepository.getAllWorkouts()
            .find { it.details.externalData == externalData }!!
    }

    @LogRepository
    override fun saveWorkoutsToLibrary(libraryContainer: LibraryContainer, workouts: List<Workout>) {
        throw PlatformException(Platform.TRAINING_PEAKS, "TP doesn't support workout creation")
    }

    @LogRepository
    override fun deleteWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate) {
        val userId = trainingPeaksUserRepository.getUser().userId
        getWorkoutsFromCalendar(startDate, endDate).forEach {
            trainingPeaksWorkoutApiClient.deleteWorkout(userId, it.details.externalData.trainingPeaksId!!)
        }
    }

    private fun saveWorkoutToCalendar(workout: Workout) {
        val createRequest: CreateTPWorkoutRequestDTO
        val structureStr = if (workout.structure != null) ToTPStructureConverter.toStructureString(objectMapper, workout.structure) else null
        val athleteId = trainingPeaksUserRepository.getUser().userId
        createRequest = CreateTPWorkoutRequestDTO.planWorkout(
            athleteId, workout, structureStr
        )
        trainingPeaksWorkoutApiClient.createAndPlanWorkout(athleteId, createRequest)
    }

    private fun getWorkoutsFromTPPlan(libraryContainer: LibraryContainer): List<Workout> {
        val planId = libraryContainer.externalData.trainingPeaksId!!
        val planStartDateShift = trainingPeaksConfigurationRepository.getConfiguration().planDaysShift
        val planStartDate = Date.thisMonday()
        val planApplyDate = planStartDate.plusDays(planStartDateShift)
        val response = trainingPeaksPlanRepository.applyPlan(planId, planApplyDate)

        try {
            val planEndDate = response.endDate.toLocalDate()

            val workouts = getWorkoutsFromCalendar(planApplyDate, planEndDate).map {
                it.withDate(it.date!!.minusDays(planStartDateShift))
            }
            return workouts
        } catch (e: Exception) {
            throw e
        } finally {
            trainingPeaksPlanRepository.removeAppliedPlan(response.appliedPlanId)
        }
    }

    private fun getWorkoutsFromTPLibrary(library: LibraryContainer): List<Workout> {
        return tpWorkoutLibraryRepository.getLibraryWorkouts(library.externalData.trainingPeaksId!!)
    }

    private fun getNoteEndDateForFilter(startDate: LocalDate, endDate: LocalDate): LocalDate =
        if (startDate == endDate) endDate.plusDays(1) else endDate

}
