package config.mock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IntervalsWorkoutApiClient
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.EventRequestDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IntervalsEventCommentDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IntervalsEventDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.WorkoutRequestDTO
import java.io.InputStream

class IntervalsWorkoutApiClientMock(
    objectMapper: ObjectMapper,
    eventsResponse: InputStream
) : IntervalsWorkoutApiClient {
    private val events: List<IntervalsEventDTO> = objectMapper.readValue(
        eventsResponse,
        object : TypeReference<List<IntervalsEventDTO>>() {}) as List<IntervalsEventDTO>

    override fun createWorkouts(athleteId: String, requests: List<WorkoutRequestDTO>) {
        TODO("Not yet implemented")
    }

    override fun createEvent(athleteId: String, eventRequestDTO: EventRequestDTO) {
        TODO("Not yet implemented")
    }

    override fun getEvents(
        athleteId: String,
        startDate: String,
        endDate: String,
        powerRange: Float,
        hrRange: Float,
        paceRange: Float
    ): List<IntervalsEventDTO> = events

    override fun getEvents(eventId: String): IntervalsEventDTO {
        TODO("Not yet implemented")
    }

    override fun createComment(requestDTO: IntervalsEventCommentDTO) {
        TODO("Not yet implemented")
    }

    override fun updateEvent(
        athleteId: String,
        eventId: Long,
        requestDTO: EventRequestDTO
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteEvent(athleteId: String, eventId: Long) {
        TODO("Not yet implemented")
    }

}
