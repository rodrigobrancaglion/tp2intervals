package org.freekode.tp2intervals.integration.platform.myfitnesspal.wellness

import org.freekode.tp2intervals.integration.platform.myfitnesspal.configuration.dto.MfpConfiguration
import org.freekode.tp2intervals.integration.platform.myfitnesspal.wellness.dto.MfpNutritionDTO
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MfpDiaryClient(
    @Value("\${app.mfp.api-url}") private val baseUrl: String,
) {
    private val log = LoggerFactory.getLogger(MfpDiaryClient::class.java)

    companion object {
        private const val DIARY_PATH = "/food/diary"
    }

    fun getDailyTotals(config: MfpConfiguration, startDate: LocalDate, endDate: LocalDate): List<MfpNutritionDTO> {
        val cookies = config.toCookieMap()
        val username = config.username!!

        val results = mutableListOf<MfpNutritionDTO>()
        var current = startDate
        while (!current.isAfter(endDate)) {
            val dto = fetchDay(username, current, cookies)
            if (dto != null) results.add(dto)
            current = current.plusDays(1)
        }
        return results
    }

    private fun fetchDay(username: String, date: LocalDate, cookies: Map<String, String>): MfpNutritionDTO? {
        val url = "$baseUrl$DIARY_PATH/$username?date=$date"
        log.debug("MFP fetching diary: $url")
        val doc: Document = Jsoup.connect(url)
            .cookies(cookies)
            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36")
            .timeout(15_000)
            .get()
        return parseTotals(doc, date)
    }

    private fun parseTotals(doc: Document, date: LocalDate): MfpNutritionDTO? {
        // MFP structure: tr.total (first row = Totals, second = Daily Goal, third = Remaining)
        // Columns by fixed index: [0]=label, [1]=calories, [2]=carbs, [3]=fat, [4]=protein
        // Calories: plain td text. Carbs/Fat/Protein: span.macro-value inside td
        val totalRows = doc.select("tr.total")
        if (totalRows.isEmpty()) return null

        val totalRow = totalRows.first()!!
        val cells = totalRow.select("td")

        fun cellValue(index: Int): Double? {
            val cell = cells.getOrNull(index) ?: return null
            val macroSpan = cell.selectFirst("span.macro-value")
            val text = (macroSpan?.text() ?: cell.text()).replace(",", "").trim()
            return text.toDoubleOrNull()
        }

        val calories = cellValue(1)
        val carbohydrates = cellValue(2)
        val fat = cellValue(3)
        val protein = cellValue(4)

        log.info("MFP [$date]: calories=$calories carbs=$carbohydrates fat=$fat protein=$protein")

        if (calories == null || calories == 0.0) {
            log.info("MFP [$date]: skipping empty diary")
            return null
        }

        return MfpNutritionDTO(
            date = date.toString(),
            calories = calories,
            carbohydrates = carbohydrates,
            fat = fat,
            protein = protein
        )
    }

}
