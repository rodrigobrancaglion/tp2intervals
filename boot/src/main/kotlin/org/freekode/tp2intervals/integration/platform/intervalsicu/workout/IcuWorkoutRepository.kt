package org.freekode.tp2intervals.integration.platform.intervalsicu.workout

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.librarycontainer.LibraryContainer
import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.domain.workout.WorkoutDetails
import org.freekode.tp2intervals.integration.PlatformException
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.IcuUserApiClient
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.freekode.tp2intervals.integration.provider.workout.IWorkoutRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class IcuWorkoutRepository(
    private val icuWorkoutApiClient: IcuWorkoutApiClient,
    private val icuUserApiClient: IcuUserApiClient,
    private val icuConfigurationRepository: IcuConfigurationRepository,
    private val icuWorkoutConverter: IcuWorkoutConverter,
    private val icuEventConverter: IcuEventConverter,
) : IWorkoutRepository {

     private val logger = AppLogger.get(this.javaClass)
    private val maxWorkoutsToSave = 10

    override fun platform() = Platform.INTERVALS

    override fun saveWorkoutsToCalendar(workouts: List<Workout>) {
        val athleteId = icuConfigurationRepository.getConfiguration().athleteId
        val athleteProfileDTO = icuUserApiClient.getUser(athleteId)

        workouts.forEach {
            val request = icuEventConverter.createIcuEventEx(it, athleteProfileDTO)
            icuWorkoutApiClient.createEvent(athleteId, request)
        }
    }

    override fun saveWorkoutsToLibrary(libraryContainer: LibraryContainer, workouts: List<Workout>) {
        val athleteId = icuConfigurationRepository.getConfiguration().athleteId
        val athleteProfileDTO = icuUserApiClient.getUser(athleteId)

        for (fromIndex in workouts.indices step maxWorkoutsToSave) {
            val toIndex =
                if (fromIndex + maxWorkoutsToSave >= workouts.size) workouts.size else fromIndex + maxWorkoutsToSave

            val workoutsToSave = workouts.subList(fromIndex, toIndex)
            val requests =
                workoutsToSave.map { icuWorkoutConverter.createIcuWorkoutEx(libraryContainer, it, athleteProfileDTO) }
            icuWorkoutApiClient.createWorkouts(icuConfigurationRepository.getConfiguration().athleteId, requests)
        }
    }

    override fun getWorkoutsFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Workout> {
        val events = getWorkouts(startDate, endDate)
        return events
            .mapNotNull { toEvent(it) }
    }

    fun getWorkouts(startDate: LocalDate, endDate: LocalDate): List<IcuEventEx> {
        val configuration = icuConfigurationRepository.getConfiguration()
       return icuWorkoutApiClient.getEvents(
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

    fun deleteEvent(eventId: Long) {
        val athleteId = icuConfigurationRepository.getConfiguration().athleteId
        logger.infoL3In("Deleting event id=$eventId")
        icuWorkoutApiClient.deleteEvent(athleteId, eventId)
    }

    private fun toEvent(eventDTO: IcuEventEx): Workout? {
        return toWorkout(eventDTO)
    }

    private fun toWorkout(eventDTO: IcuEventEx): Workout? {
        return try {
            icuWorkoutConverter.toWorkout(eventDTO)
        } catch (e: PlatformException) {
            logger.warnL3In("Can't convert a workout ${eventDTO.name} on ${eventDTO.start_date_local}, skipping...", e)
            return null
        }
    }
}
