package config.mock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.*
import java.io.InputStream

class IntervalsWorkoutApiClientMock(
    objectMapper: ObjectMapper,
    eventsResponse: InputStream
) : IntervalsWorkoutApiClient {
    private val events: List<IntervalsEventDTO> = objectMapper.readValue(
        eventsResponse,
        object : TypeReference<List<IntervalsEventDTO>>() {}) as List<IntervalsEventDTO>

    override fun createWorkouts(athleteId: String, requests: List<CreateWorkoutRequestDTO>) {
        TODO("Not yet implemented")
    }

    override fun createEvent(athleteId: String, createEventRequestDTO: CreateEventRequestDTO) {
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

    override fun createComment(requestDTO: IntervalsEventCommentDTO) {
        TODO("Not yet implemented")
    }

}
