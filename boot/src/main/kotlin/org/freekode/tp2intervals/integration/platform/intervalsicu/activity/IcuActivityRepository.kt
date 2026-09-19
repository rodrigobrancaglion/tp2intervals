package org.freekode.tp2intervals.integration.platform.intervalsicu.activity

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.ExternalData
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto.IcuActivityResponse
import org.freekode.tp2intervals.integration.platform.intervalsicu.activity.dto.IcuNewActivityMsg
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IcuConfigurationRepository
import org.freekode.tp2intervals.integration.platform.intervalsicu.workout.IcuWorkoutRepository
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class IcuActivityRepository(
    private val icuActivityApiClient: IcuActivityApiClient,
    private val icuConfigurationRepository: IcuConfigurationRepository,
    private val icuWorkoutRepository: IcuWorkoutRepository
) : IActivityRepository {
    private val logger = AppLogger.get(this.javaClass)

    override fun platform() = Platform.INTERVALS

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        activities.forEach { activity ->
//            CompletableFuture.runAsync {
                try {
                    var activityId = ""
                    if (activity.resource != null) {
                        // Upload .fit file to create a full activity in ICU
                        val response = createActivity(activity)
                        if (response != null) {
                            removeDuplicateWorkouts(response, activity, types)
                            activityId = response.id
                        }
                    }
                    else {
                        // No .fit available: update RPE/FEEL only if the activity already exists in ICU
                        updateActivity(activity, types)
                        activityId = activity.workoutId.toString()
                    }

                    saveComments(activityId, activity)
                } catch (e: Exception) {
                    logger.errorL3In("Intervals - Error saving activity ${activity.workoutId} : ${e.message}", e)
                }
//            }
        }
    }

    private fun saveComments(activityId: String, activity: Activity) {
        activity.workoutComments.forEach { activityComment ->
            if (!activityComment.comment.isNullOrEmpty()) {
                try {
                    icuActivityApiClient.saveComment(activityId,IcuNewActivityMsg(content = activityComment.comment))
                    logger.infoL4In("Comment saved for activity $activityId")
                } catch (e: Exception) {
                    logger.errorL4In("Error saving comment for activity $activityId : ${e.message}")
                }
            }
        }
    }

    private fun createActivity(activity: Activity): IcuActivityResponse? {
        val athleteId = icuConfigurationRepository.getConfiguration().athleteId

        val fitBytes = Base64.getDecoder().decode(activity.resource)
        val fileName = activity.fileName ?: "${activity.workoutId}.fit"
        val multipart = ByteArrayMultipartFile(fitBytes, fileName)
        val name = activity.description ?: activity.title ?: "" //Mantem o nome original do treino
        val description = activity.title ?: "Activity ${activity.workoutId}" //Se a atividade for ROUVY -insere na desscricao o nome da Rota

        logger.infoL3In("Creating activity in ICU via .fit upload: workoutId=${activity.workoutId}, fileName=$fileName, name=$name")
        return try {
            val response = icuActivityApiClient.createActivity(athleteId, name, description, multipart)
            response
        } catch (e: Exception) {
            logger.errorL3In("Error with direct FIT upload for $name: ${e.message}")
            null
        }
    }

    private fun updateActivity(activity: Activity, types: List<BaseType>) {
        val newActivityDTO = IcuToActivityConverter().toDTO(activity, types)
        icuActivityApiClient.updateActivity(activity.workoutId.toString(), newActivityDTO)
    }

    private fun removeDuplicateWorkouts(response: IcuActivityResponse, activity: Activity, types: List<BaseType>){
        val startedAt = activity.startedAt?.toLocalDate() ?: LocalDate.now()
        val workouts = icuWorkoutRepository.getWorkouts(startedAt, startedAt)

        val originalWorkout = workouts.firstOrNull { event ->
            val externalData = ExternalData.empty().fromSimpleString(event.description ?: "")
            externalData.trainingPeaksId == activity.workoutId.toString()
        }

        val fitWorkout = workouts.firstOrNull { event ->
            val externalData = ExternalData.empty().fromSimpleString(event.description ?: "")
            externalData.trainingPeaksId == null && event.id != originalWorkout?.id
        }

        if (originalWorkout != null && originalWorkout.id != null) {
            logger.infoL3In("Original workout: workoutId=${originalWorkout.id}, name=${originalWorkout.name}")
            val activityCompleted = Activity(originalWorkout.id, activity.rpe, activity.feel)
            val activityToUpdate = IcuToActivityConverter().toDTO(activityCompleted, types)
            icuActivityApiClient.updateActivity(response.id, activityToUpdate)
        }
        if (fitWorkout != null && fitWorkout.id != null) {
            logger.infoL3In("Fit workout (to delete): workoutId=${fitWorkout.id}, name=${fitWorkout.name}")
            icuWorkoutRepository.deleteEvent(fitWorkout.id)
        }
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val activities =
            icuActivityApiClient.getActivities(
                icuConfigurationRepository.getConfiguration().athleteId,
                startDate.atStartOfDay().toString(),
                endDate.atStartOfDay().plusDays(1).minusSeconds(1).toString()
            )

        val workoutsMap = icuWorkoutRepository.getWorkoutsFromCalendar(startDate, endDate)
            .associateBy { it.id }

        return activities
            .map {
                val pairedWorkout = workoutsMap[it.paired_event_id]
                IcuToActivityConverter().toDomain(it, pairedWorkout)
            }
    }
}
