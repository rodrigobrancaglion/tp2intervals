package org.freekode.tp2intervals.integration.platform.myfitnesspal.wellness

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.myfitnesspal.configuration.MfpConfigurationRepository
import org.freekode.tp2intervals.integration.provider.wellness.IWellnessRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MfpWellnessRepository(
    private val mfpDiaryClient: MfpDiaryClient,
    private val mfpConfigurationRepository: MfpConfigurationRepository,
) : IWellnessRepository {

    override fun platform() = Platform.MYFITNESSPAL

    override fun getFromCalendar(startDate: LocalDate, endDate: LocalDate): List<Wellness> {
        val config = mfpConfigurationRepository.getConfiguration()
        return mfpDiaryClient.getDailyTotals(config, startDate, endDate)
            .map { MfpWellnessConverter(it).toDomain() }
    }

    override fun saveToCalendar(wellnesses: List<Wellness?>, startDate: LocalDate, endDate: LocalDate) {
        throw UnsupportedOperationException("MyFitnessPal is read-only — cannot save wellness data to MFP")
    }
}
