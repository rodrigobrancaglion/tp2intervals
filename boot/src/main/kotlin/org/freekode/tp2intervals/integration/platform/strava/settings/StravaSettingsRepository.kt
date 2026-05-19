package org.freekode.tp2intervals.integration.platform.strava.settings

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.settings.PowerZone
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class StravaSettingsRepository(
    private val stravaSettingsApiClient: StravaSettingsApiClient
) : ISettingsRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun platform() = Platform.STRAVA

    override fun getPowerZones(): Pair<Int, List<PowerZone>> {
        throw UnsupportedOperationException("Getting power zones from Strava is not supported")
    }

    override fun savePowerZones(threshold: Int, zones: List<PowerZone>) {
        throw UnsupportedOperationException("Updating power zones from Strava is not supported")
//        val payload = mapOf<String, Any?>(
//            "ftp" to threshold
//        )
//
//        log.info("Sending Payload (FTP=$threshold) to Strava...")
//        stravaSettingsApiClient.updateAthlete(payload)
    }
}
