package org.freekode.tp2intervals.integration.platform.trainerroad.member

import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository

@CacheConfig(cacheNames = ["trUsernameCache"])
@Repository
class TRUsernameRepository(
    private val trainerRoadMemberApiClient: TrainerRoadMemberApiClient,
) {
    @Cacheable
    fun getUsername(): String {
        return trainerRoadMemberApiClient.getMember().Username!!
    }
}
