package org.freekode.tp2intervals.integration.platform.trainerroad.activity

import org.freekode.tp2intervals.domain.BaseType
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.activity.Activity
import org.freekode.tp2intervals.integration.platform.trainerroad.TRApiClientService
import org.freekode.tp2intervals.integration.platform.trainerroad.member.TRUsernameRepository
import org.freekode.tp2intervals.integration.provider.activity.IActivityRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TrainerRoadActivityRepository(
    private val trUsernameRepository: TRUsernameRepository,
    private val trApiClientService: TRApiClientService,
) : IActivityRepository {
    override fun platform() = Platform.TRAINER_ROAD

    override fun saveActivities(activities: List<Activity>, types: List<BaseType>) {
        TODO("Not yet implemented")
    }

    override fun getActivities(startDate: LocalDate, endDate: LocalDate): List<Activity> {
        val memberId = trUsernameRepository.getMemberId()
        return trApiClientService.getActivities(memberId, startDate, endDate)
    }
}
