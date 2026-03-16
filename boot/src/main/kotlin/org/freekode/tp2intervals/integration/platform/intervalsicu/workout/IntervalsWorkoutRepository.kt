package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.IntervalsUserApiClient
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.provider.workout.IWorkoutRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IntervalsWorkoutRepository(
    private val intervalsWorkoutApiClient: IntervalsWorkoutApiClient,
    private val intervalsUserApiClient: IntervalsUserApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository,
    private val toIntervalsWorkoutConverter: ToIntervalsWorkoutConverter,
) : IWorkoutRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val maxWorkoutsToSave = 10

    override fun platform() = Platform.INTERVALS

    override fun saveWorkoutsToCalendar(workouts: List<Workout>) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId
        val athleteProfileDTO = intervalsUserApiClient.getUser(athleteId)

        workouts.forEach {
            val request = toIntervalsWorkoutConverter.createEventRequestDTO(it, athleteProfileDTO)
            intervalsWorkoutApiClient.createEvent(athleteId, request)
        }
    }

    override fun saveWorkoutsToLibrary(libraryContainer: LibraryContainer, workouts: List<Workout>) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId
        val athleteProfileDTO = intervalsUserApiClient.getUser(athleteId)

        for (fromIndex in workouts.indices step maxWorkoutsToSave) {
            val toIndex =
                if (fromIndex + maxWorkoutsToSave >= workouts.size) workouts.size else fromIndex + maxWorkoutsToSave

            val workoutsToSave = workouts.subList(fromIndex, toIndex)
            val requests =
                workoutsToSave.map { toIntervalsWorkoutConverter.createWorkoutRequestDTO(libraryContainer, it, athleteProfileDTO) }
            intervalsWorkoutApiClient.createWorkouts(intervalsConfigurationRepository.getConfiguration().athleteId, requests)
        }
    }

    override fun getWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Workout> {
        val events = getWorkouts(startDate, endDate)
        return events
            .mapNotNull { toEvent(it) }
    }

    fun getWorkouts(startDate: LocalDate, endDate: LocalDate): List<IntervalsEventDTO> {
        val configuration = intervalsConfigurationRepository.getConfiguration()
       return intervalsWorkoutApiClient.getEvents(
            configuration.athleteId,
            startDate.toString(),
            endDate.toString(),
            configuration.powerRange,
            configuration.hrRange,
            configuration.paceRange,
        )
    }

    override fun getWorkoutFromLibrary(externalData: ExternalData): Workout {
        TODO("Not yet implemented")
    }

    override fun findWorkoutsFromLibraryByName(name: String): List<WorkoutDetails> {
        TODO("Not yet implemented")
    }

    override fun getWorkoutsFromLibrary(libraryContainer: LibraryContainer): List<Workout> {
        TODO("Not yet implemented")
    }

    override fun deleteWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate) {
        TODO("Not yet implemented")
    }

    fun updateEventPairedActivity(eventId: Long, activityId: String) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId
        log.info("Updating paired_activity_id for eventId=$eventId with activityId=$activityId")
        intervalsWorkoutApiClient.updateEvent(
            athleteId, eventId, EventRequestDTO(
                start_date_local = null, name = null, category = null, type = null,
                description = null, moving_time = null, icu_training_load = null,
                attachments = null, paired_activity_id = activityId
            )
        )
    }

    fun deleteEvent(eventId: Long) {
        val athleteId = intervalsConfigurationRepository.getConfiguration().athleteId
        log.info("Deleting event id=$eventId")
        intervalsWorkoutApiClient.deleteEvent(athleteId, eventId)
    }

    private fun toEvent(eventDTO: IntervalsEventDTO): Workout? {
        return toWorkout(eventDTO)
    }

    private fun toWorkout(eventDTO: IntervalsEventDTO): Workout? {
        return try {
            IntervalsWorkoutConverter(eventDTO).toWorkout()
        } catch (e: PlatformException) {
            log.warn("Can't convert a workout ${eventDTO.name} on ${eventDTO.start_date_local}, skipping...", e)
            return null
        }
    }
}
