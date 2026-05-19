package org.freekode.tp2intervals.integration.platform.trainingpeaks.settings

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.settings.PowerZone
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.TrainingPeaksUserRepository
import org.freekode.tp2intervals.integration.provider.settings.ISettingsRepository
import org.springframework.stereotype.Repository

@Repository
class TrainingpeaksSettingsRepository(
    private val trainingPeaksSettingsApiClient: TrainingPeaksSettingsApiClient,
    private val trainingPeaksUserRepository: TrainingPeaksUserRepository
) : ISettingsRepository {
    override fun platform() = Platform.TRAINING_PEAKS

    override fun getPowerZones(): Pair<Int, List<PowerZone>> {
        val userId = trainingPeaksUserRepository.getUser().userId
            ?: throw IllegalStateException("TrainingPeaks userId not found")
        val tpSettings = trainingPeaksSettingsApiClient.getSettings(userId)

        val powerZonesItem = tpSettings.powerZones.firstOrNull()
            ?: throw IllegalStateException("No power zones found in TrainingPeaks settings for user $userId")

        val threshold = powerZonesItem.threshold.toInt()
        val zones = powerZonesItem.zones.map {
            PowerZone(minimum = it.minimum, maximum = it.maximum)
        }

        return Pair(threshold, zones)
    }

    override fun savePowerZones(threshold: Int, zones: List<PowerZone>) {
        throw UnsupportedOperationException("Saving power zones to TrainingPeaks is not supported")
    }
}
