package org.freekode.tp2intervals.integration.platform.trainingpeaks

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import feign.RequestInterceptor
import feign.codec.Encoder
import org.freekode.tp2intervals.integration.platform.trainingpeaks.token.TrainingPeaksTokenRepository
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class TrainingPeaksApiClientConfig(
    private val trainingPeaksTokenRepository: TrainingPeaksTokenRepository,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val token = trainingPeaksTokenRepository.getToken()
            template.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
        }
    }

    /**
     * Custom encoder that ALWAYS includes null values in the JSON.
     * Essential for TP V6 API which fails with 500 if fields like 'distance' are missing.
     */
    @Bean
    fun feignEncoder(): Encoder {
        val customMapper = objectMapper.copy()
            .setSerializationInclusion(JsonInclude.Include.ALWAYS)

        val jacksonConverter = MappingJackson2HttpMessageConverter(customMapper)
        return SpringEncoder { HttpMessageConverters(jacksonConverter) }
    }
}
