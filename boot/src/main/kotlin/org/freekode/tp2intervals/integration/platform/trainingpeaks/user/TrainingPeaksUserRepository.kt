package org.freekode.tp2intervals.integration.platform.trainingpeaks.user

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@CacheConfig(cacheNames = ["tpUserCache"])
@Component
class TrainingPeaksUserRepository(
    private val trainingPeaksUserApiClient: TrainingPeaksUserApiClient,
) {
    @Cacheable(key = "'singleton'")
    fun getUser(): TrainingPeaksUser {
        val dto = trainingPeaksUserApiClient.getUser()
        return TrainingPeaksUser(dto.userId!!, dto.accountStatus.isAthlete, dto.accountStatus.isPremium)
    }
}
