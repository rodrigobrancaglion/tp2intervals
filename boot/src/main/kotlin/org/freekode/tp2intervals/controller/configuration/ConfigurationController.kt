package org.freekode.tp2intervals.controller.configuration

import org.freekode.tp2intervals.domain.Platform
import org.freekode.tp2intervals.domain.workout.structure.StepModifier
import org.freekode.tp2intervals.dto.ErrorResponse
import org.freekode.tp2intervals.dto.confguration.ConfigurationResponse
import org.freekode.tp2intervals.dto.confguration.UpdateConfigurationRequest
import org.freekode.tp2intervals.service.ConfigurationService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ConfigurationController(
    private val configurationService: ConfigurationService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/api/configuration")
    fun getConfigurations(): ConfigurationResponse {
        log.debug("Received request for getting all configurations")
        val configurations = configurationService.getConfigurations()
        return ConfigurationResponse(configurations.configMap)
    }

    @PutMapping("/api/configuration")
    fun updateConfiguration(@RequestBody requestDTO: UpdateConfigurationRequest): ResponseEntity<ErrorResponse> {
        log.debug("Received request for updating configuration: {}", requestDTO)
        val errors = configurationService.updateConfiguration(UpdateConfigurationRequest(requestDTO.config))
        if (errors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse(errors.joinToString()))
        }
        return ResponseEntity.ok().build()
    }

    @GetMapping("/api/configuration/intervals-step-modifiers")
    fun getIntervalsStepModifiers(): List<StepModifier> {
        return StepModifier.entries
    }

    @GetMapping("/api/configuration/platform")
    fun getAllPlatformInfo() =
        configurationService.platformInfo()

    @GetMapping("/api/configuration/{platform}")
    fun getConfigurations(@PathVariable platform: Platform) =
        configurationService.platformInfo(platform)

    @PostMapping("/api/configuration/wahoo/auth")
    fun exchangeWahooCode(@RequestParam code: String, @RequestParam redirectUri: String): ResponseEntity<Any> {
        return try {
            configurationService.exchangeWahooCode(code, redirectUri)
            val configurations = configurationService.getConfigurations()
            ResponseEntity.ok().body(ConfigurationResponse(configurations.configMap))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(ErrorResponse(e.message))
        }
    }
}
