package org.freekode.tp2intervals.integration.platform.intervalsicu.wellness

import org.freekode.tp2intervals.domain.WellnessType
import org.freekode.tp2intervals.domain.wellness.Wellness
import org.freekode.tp2intervals.integration.platform.intervalsicu.wellness.dto.IcuWellness
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IcuWellnessConverter {
    private var wellnessDTO: IcuWellness? = null
    private var wellness: Wellness? = null

    // Construtor para quando você tem o DTO (vindo do Intervals)
    constructor(wellnessDTO: IcuWellness) {
        this.wellnessDTO = wellnessDTO
    }

    // Construtor para quando você tem o Domain (para enviar ao Intervals)
    constructor(wellness: Wellness?) {
        this.wellness = wellness
    }

    fun toDTO(): IcuWellness {
        val outputFormatter = DateTimeFormatter.ofPattern(Wellness.DATE_FORMAT)

        // 1. Calculamos a data formatada corretamente
        val formattedDate = wellness?.date?.let { dateStr ->
            try {
                val date = if (dateStr.contains("T")) {
                    java.time.OffsetDateTime.parse(dateStr).toLocalDate()
                } else {
                    // Pega os 10 primeiros caracteres e transforma em LocalDate para validar
                    LocalDate.parse(dateStr.take(10))
                }
                date.format(outputFormatter)
            } catch (e: Exception) {
                // Se falhar, tenta retornar apenas os 10 caracteres ou o que estiver disponível
                dateStr.take(10)
            }
        }

        // 2. Usamos a 'formattedDate' no campo 'id' do DTO
        return IcuWellness(
            id = formattedDate ?: "",
            weight = wellness?.weight,
            kcalConsumed = wellness?.calories?.toInt(),
            carbohydrates = wellness?.carbohydrates,
            protein = wellness?.protein,
            fatTotal = wellness?.fat
        )
    }

    fun toDomain(): Wellness {
        return Wellness(
            date = wellnessDTO?.id,
            type = WellnessType.WEIGHT,
            weight = wellnessDTO?.weight ?: -1.0,
            calories = wellnessDTO?.kcalConsumed?.toDouble(),
            carbohydrates = wellnessDTO?.carbohydrates,
            protein = wellnessDTO?.protein,
            fat = wellnessDTO?.fatTotal
        )
    }
}
