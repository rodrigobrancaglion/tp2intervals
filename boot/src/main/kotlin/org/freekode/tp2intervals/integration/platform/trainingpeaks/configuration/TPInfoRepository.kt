package org.freekode.tp2intervals.integration.platform.trainingpeaks.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.config.PlatformInfo
import org.freekode.tp2intervals.domain.config.PlatformInfoRepository
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TPUserRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["platformInfoCache"])
class TPInfoRepository(
    private val TPConfigurationRepository: TPConfigurationRepository,
    private val TPUserRepository: TPUserRepository,
) : PlatformInfoRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    @Cacheable(keyGenerator = "userKeyGenerator")
    override fun platformInfo(): PlatformInfo {
        val isValid = TPConfigurationRepository.isValid()
        if (!isValid) {
            return PlatformInfo(mapOf("isValid" to false))
        }

        val user = TPUserRepository.getUser()
        val infoMap = mapOf(
            "isValid" to true,
            "isAthlete" to user.isAthlete,
            "isPremium" to user.isPremium
        )
        return PlatformInfo(infoMap)
    }
}
