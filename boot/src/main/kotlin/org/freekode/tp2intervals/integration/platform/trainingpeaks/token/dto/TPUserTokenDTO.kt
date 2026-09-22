package org.freekode.tp2intervals.integration.platform.trainingpeaks.token.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TPUserTokenDTO(
    var accessToken: String?
) {
    @JsonProperty("token")
    private fun mapAccessToken(map: Map<String, Any>) {
        this.accessToken = map["access_token"].toString()
    }
}