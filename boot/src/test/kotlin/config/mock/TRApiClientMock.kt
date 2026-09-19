package config.mock

import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.integration.platform.trainerroad.TRApiClient
import org.freekode.tp2intervals.integration.platform.trainerroad.activity.dto.TrainerRoadActivityDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRFindWorkoutsRequestDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRFindWorkoutsResponseDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRTimelineDTO
import org.freekode.tp2intervals.integration.platform.trainerroad.workout.dto.TRWorkoutResponseDTO
import org.springframework.core.io.Resource
import java.io.InputStream

class TRApiClientMock(
    objectMapper: ObjectMapper,
    simpleWorkoutResponse: InputStream,
    complexWorkoutResponse: InputStream,
    anotherWorkoutResponse: InputStream,
) : TRApiClient {
    private val simpleWorkout: TRWorkoutResponseDTO = objectMapper.readValue(
        simpleWorkoutResponse, TRWorkoutResponseDTO::class.java
    )
    private val complexWorkout: TRWorkoutResponseDTO = objectMapper.readValue(
        complexWorkoutResponse, TRWorkoutResponseDTO::class.java
    )
    private val anotherWorkout: TRWorkoutResponseDTO = objectMapper.readValue(
        anotherWorkoutResponse, TRWorkoutResponseDTO::class.java
    )

    override fun getTimeline(memberId: Long, startDate: String, endDate: String): TRTimelineDTO {
        return TRTimelineDTO()
    }

    override fun getActivities(memberId: Long, ids: String): List<TrainerRoadActivityDTO> {
        return emptyList()
    }

    override fun findWorkouts(requestDTO: TRFindWorkoutsRequestDTO): TRFindWorkoutsResponseDTO {
        TODO("Not yet implemented")
    }

    override fun getWorkout(workoutId: String): TRWorkoutResponseDTO {
        return when (workoutId) {
            "simple" -> simpleWorkout
            "complex" -> complexWorkout
            "another" -> anotherWorkout
            else -> throw IllegalStateException("unknown workout id: $workoutId")
        }
    }

    override fun exportFit(activityId: String): Resource {
        TODO("Not yet implemented")
    }
}
