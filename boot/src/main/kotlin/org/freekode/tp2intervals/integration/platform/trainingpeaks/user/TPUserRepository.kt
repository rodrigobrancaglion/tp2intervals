package org.freekode.tp2intervals.integration.platform.trainingpeaks.user

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@CacheConfig(cacheNames = ["tpUserCache"])
@Component
class TPUserRepository(
    private val TPUserApiClient: TPUserApiClient,
) {
    @Cacheable(key = "'singleton'")
    fun getUser(): TPUser {
        val dto = TPUserApiClient.getUser()
        return TPUser(dto.userId!!, dto.accountStatus.isAthlete, dto.accountStatus.isPremium)
    }
}
