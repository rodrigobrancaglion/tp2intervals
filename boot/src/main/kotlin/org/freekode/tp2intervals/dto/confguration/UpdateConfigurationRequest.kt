package org.freekode.tp2intervals.dto.confguration

data class UpdateConfigurationRequest(
    val config: Map<String, String?>
) {
    fun getByPrefix(prefix: String): Map<String, String?> {
        return config.filter { it.key.startsWith(prefix) }
    }
}