package org.freekode.tp2intervals.integration.platform.trainingpeaks.workout

import org.freekode.tp2intervals.domain.workout.Workout
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto.IntervalsAthleteProfileDTO
import org.freekode.tp2intervals.integration.platform.intervalsicu.athlete.dto.SportSettingsDTO
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.math.round

@Service
class TPPowerCalculationService(
) {
    /**
     * Calculates the target intensity string based on Intervals.icu settings.
     * All comments are in English as requested.
     */
    fun getTargetIntensity(workout: Workout, profile: IntervalsAthleteProfileDTO): String {
        val targetIf = workout.details.ifPlanned ?: 0.0
        val duration = workout.details.duration ?: Duration.ZERO

        if (targetIf == 0.0 && duration == Duration.ZERO) {
            return ""
        }
        val roundedIf = round(targetIf * 100) / 100.0

        // 1. Get the specific settings for Cycling (Ride) from the profile
        val rideSettings = profile.findRideSettings()
            ?: throw IllegalStateException("Ride settings not found in Intervals profile")

        val ftp = rideSettings.ftp ?: 0
        val durationStr = duration.toReadableString()

        // 2. Calculate target watts: IF * FTP
        val targetWatts = (roundedIf * ftp).toInt()

        val powerRange = getPrecisionPowerRange(targetWatts)

        // 3. Get description based on Intervals percentages and names
        val zoneInfo = getIntervalsZoneDescription(targetWatts, ftp, rideSettings)

        return "- $durationStr $powerRange ($zoneInfo)"
    }

    /**
     * Finds the correct zone name based on percentage of FTP.
     * Intervals zones list (e.g., [55, 75, 90...]) represents the upper limit % of each zone.
     */
    private fun getIntervalsZoneDescription(targetWatts: Int, ftp: Int, settings: SportSettingsDTO): String {
        if (ftp <= 0) return "Unknown Zone"

        // Calculate the intensity percentage relative to FTP
        val intensityPercentage = (targetWatts.toDouble() / ftp) * 100

        /**
         * Intervals logic: powerZones are upper limits.
         * Example: [55, 75, 90] means:
         * Z1: 0-55%, Z2: 56-75%, Z3: 76-90%
         */
        val zones = settings.powerZones ?: emptyList()

        // Use the 'zones' variable directly (It is 'List<Int>', not 'List<Int>?')
        // Now zoneIndex will be a perfect 'Int'
        val zoneIndex = zones.indexOfFirst { intensityPercentage <= it }

        // Simple comparison
        if (zoneIndex == -1) return "Unknown Zone"

        // Since zoneIndex is a plain Int, no need for ?.plus(1)
        val prefix = "Z${zoneIndex + 1}"

        val label = settings.powerZoneNames?.getOrNull(zoneIndex) ?: "Custom Zone"

        return "$prefix - $label"
    }

    private fun getPrecisionPowerRange(targetWatts: Int): String {
        val rangeMin = targetWatts - 5
        val rangeMax = targetWatts + 5
        return "${rangeMin}-${rangeMax}W"
    }

    fun Duration.toReadableString(): String {
        val hours = this.toHours()
        val minutes = this.toMinutesPart()
        return when {
            hours > 0 && minutes > 0 -> "${hours}h${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
}