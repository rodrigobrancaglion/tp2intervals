package org.freekode.tp2intervals.integration.platform.trainingpeaks.user

import org.freekode.tp2intervals.integration.platform.trainingpeaks.TPApiClientConfig
import org.freekode.tp2intervals.integration.platform.trainingpeaks.user.dro.TPUserDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    value = "TrainingPeaksUserApiClient",
    url = "\${app.training-peaks.api-url}",
    dismiss404 = true,
    primary = false,
    configuration = [TPApiClientConfig::class]
)
interface TPUserApiClient {
    @GetMapping("/users/v3/user")
    fun getUser(): TPUserDTO
}
