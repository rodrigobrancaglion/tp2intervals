package org.freekode.tp2intervals.integration.platform.rouvy

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@FeignClient(name = "rouvy-firestore", url = "https://firestore.googleapis.com/v1")
interface RouvyFirestoreClient {

    @GetMapping("/projects/{projectId}/databases/(default)/documents/users/{uid}/rideResults/{activityId}")
    fun getActivity(
        @PathVariable("projectId") projectId: String,
        @PathVariable("uid") uid: String,
        @PathVariable("activityId") activityId: String,
        @RequestHeader("Authorization") token: String
    ): Map<String, Any>
}

@FeignClient(name = "rouvy-auth", url = "https://securetoken.googleapis.com/v1")
interface RouvyAuthClient {

    @PostMapping(
        value = ["/token"],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun refreshToken(
        @RequestParam("key") apiKey: String,
        @RequestBody body: String
    ): Map<String, Any>
}
