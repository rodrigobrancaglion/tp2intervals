package config.mock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IcuWorkoutApiClient
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.dto.IcuWorkoutEx
import java.io.InputStream

class IcuWorkoutApiClientMock(
    objectMapper: ObjectMapper,
    eventsResponse: InputStream
) : IcuWorkoutApiClient {
    private val events: List<IcuEventEx> = objectMapper.readValue(
        eventsResponse,
        object : TypeReference<List<IcuEventEx>>() {}) as List<IcuEventEx>

    override fun createWorkouts(athleteId: String, requests: List<IcuWorkoutEx>) {
        TODO("Not yet implemented")
    }

    override fun createEvent(athleteId: String, eventRequestDTO: IcuEventEx) {
        TODO("Not yet implemented")
    }

    override fun getEvents(
        athleteId: String,
        startDate: String,
        endDate: String,
        powerRange: Float,
        hrRange: Float,
        paceRange: Float
    ): List<IcuEventEx> = events

    override fun getEvents(eventId: String): IcuEventEx {
        TODO("Not yet implemented")
    }

    override fun updateEvent(
        athleteId: String,
        eventId: Long,
        requestDTO: IcuEventEx
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteEvent(athleteId: String, eventId: Long) {
        TODO("Not yet implemented")
    }

    override fun createEventsBulk(
        athleteId: String,
        requests: List<org.freekode.tp2intervals.integration.platform.intervalsicu.event.dto.IcuEventEx>
    ) {
        TODO("Not yet implemented")
    }

}
