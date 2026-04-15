package config.mock

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.TrainingPeaksWorkoutApiClient
import org.freekode.tp2intervals.integration.platform.trainingpeaks.workout.dto.*
import org.springframework.core.io.Resource
import java.io.InputStream

class TrainingPeaksWorkoutApiClientMock(
    objectMapper: ObjectMapper,
    workoutsResponse: InputStream,
) : TrainingPeaksWorkoutApiClient {
    private val workouts: List<TPWorkoutCalendarDTO> = objectMapper.readValue(
        workoutsResponse,
        object : TypeReference<List<TPWorkoutCalendarDTO>>() {}) as List<TPWorkoutCalendarDTO>

    override fun getWorkouts(userId: String, startDate: String, endDate: String) = workouts

    override fun getWorkout(
        userId: String,
        workoutId: Long
    ): TPWorkoutCalendarDTO {
        TODO("Not yet implemented")
    }

    override fun getNotes(userId: String, startDate: String, endDate: String) =
        listOf<TPNoteResponseDTO>()


    override fun downloadWorkoutFit(userId: String, workoutId: String): Resource {
        TODO("Not yet implemented")
    }

    override fun getWorkoutDetails(userId: String, workoutId: Long): TPWorkoutDetailsResponseDTO {
        TODO("Not yet implemented")
    }

    override fun downloadWorkoutAttachment(userId: String, workoutId: Long, attachmentId: String): Resource {
        TODO("Not yet implemented")
    }

    override fun createAndPlanWorkout(userId: String, requestDTO: CreateTPWorkoutRequestDTO) {
        TODO("Not yet implemented")
    }

    override fun deleteWorkout(userId: String, workoutId: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun createComment(
        userId: String,
        workoutId: String,
        requestDTO: TPWorkoutCommentRequestDTO
    ): TPWorkoutCommentDTO {
        TODO("Not yet implemented")
    }
}
