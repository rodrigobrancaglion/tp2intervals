package org.freekode.tp2intervals.integration.platform.wahoo.settings

import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.settings.PowerZone
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.springframework.stereotype.Repository

@Repository
class WahooSettingsRepository(
    private val wahooSettingsApiClient: WahooSettingsApiClient,
) : ISettingsRepository {
    private val logger = AppLogger.get(this.javaClass)

    override fun platform() = Platform.WAHOO

    override fun getPowerZones(): Pair<Int, List<PowerZone>> {
        // For now, we only care about saving to Wahoo
        return Pair(0, emptyList())
    }

    override fun savePowerZones(threshold: Int, zones: List<PowerZone>) {
        logger.infoL3In("Syncing power zones to Wahoo: FTP=$threshold, Zones Count=${zones.size}")

        val currentZones = wahooSettingsApiClient.getPowerZones()
        // Workout Type ID 10 is typically Biking
        val bikingZone = currentZones.find { it.workoutTypeId == 10 }

        val params = mutableMapOf<String, Any>()
        params["power_zone[ftp]"] = threshold
        params["power_zone[zone_count]"] = zones.size

        // Wahoo expects the upper bound of each zone
        zones.forEachIndexed { index, zone ->
            val zoneNumber = index + 1
            params["power_zone[zone_$zoneNumber]"] = zone.maximum.toInt()
        }

        if (bikingZone != null && bikingZone.id != null) {
            logger.infoL3In("Updating existing Wahoo power zone: ID=${bikingZone.id}")
            wahooSettingsApiClient.updatePowerZone(bikingZone.id, params)
        } else {
            logger.infoL3In("Creating new Wahoo power zone")
            params["power_zone[workout_type_id]"] = 10
            wahooSettingsApiClient.createPowerZone(params)
        }
    }
}
