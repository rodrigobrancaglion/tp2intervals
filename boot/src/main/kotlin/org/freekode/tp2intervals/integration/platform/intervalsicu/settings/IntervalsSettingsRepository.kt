package org.freekode.tp2intervals.integration.platform.intervalsicu.settings

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.settings.PowerZone
import org.freekode.tp2intervals.integration.platform.intervalsicu.configuration.IntervalsConfigurationRepository
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class IntervalsSettingsRepository(
    private val intervalsSettingsApiClient: IntervalsSettingsApiClient,
    private val intervalsConfigurationRepository: IntervalsConfigurationRepository
) : ISettingsRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun platform() = Platform.INTERVALS

    override fun getPowerZones(): Pair<Int, List<PowerZone>> {
        throw UnsupportedOperationException("Getting power zones from Intervals.icu is not supported")
    }

    override fun savePowerZones(threshold: Int, zones: List<PowerZone>) {
        val icuConfig = intervalsConfigurationRepository.getConfiguration()
        val icuAthleteId = icuConfig.athleteId ?: throw IllegalStateException("ICU athleteId not configured")
        val icuSportSettingsId = 1311702 // Default or required ID as per instructions

        // Map domain objects back to TPZoneRangeDTO-like structure for the utility, or rewrite SettingUtils.
        // Let's rewrite the call since SettingUtils expects a threshold and generic lists.
//        val percentages = SettingUtils.calculateIcuPowerZonePercentagesFromDomain(threshold, zones)
//        val names = SettingUtils.getIcuPowerZoneNames(percentages.size)

        // Payload 1: FTP update
        val payload1 = mapOf<String, Any>("ftp" to threshold)

        // Payload 2: Power zones update
//        val payload2 = mapOf<String, Any>(
//            "power_zones" to percentages,
//            "power_zone_names" to names,
//            "sweet_spot_min" to 84,
//            "sweet_spot_max" to 97
//        )

        log.info("Sending Payload 1 (FTP) to Intervals.icu...")
        intervalsSettingsApiClient.updateSportSettings(icuAthleteId, icuSportSettingsId, payload1)

//        log.info("Sending Payload 2 (Power Zones) to Intervals.icu: $payload2")
//        intervalsSettingsApiClient.updateSportSettings(icuAthleteId, icuSportSettingsId, payload2)
    }
}
