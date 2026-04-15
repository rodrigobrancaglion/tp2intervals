package org.freekode.tp2intervals.integration.platform.rouvy

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "rouvy-riders", url = "https://riders.rouvy.com")
interface RouvyRidersClient {

    @GetMapping("/training-calendar.data")
    fun getCalendar(
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("RSC") rsc: String = "1",
        @RequestHeader("X-Forwarded-Proto") proto: String = "https",
        @RequestHeader("X-Nextjs-Data") nextData: String = "1",
        @RequestHeader("X-Requested-With") requestedWith: String = "XMLHttpRequest",
        @RequestHeader("Accept") accept: String = "text/x-component",
        @RequestHeader("Next-Router-Prefetch") prefetch: String = "1",
        @RequestHeader("User-Agent") ua: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
        @RequestHeader("Referer") referer: String = "https://riders.rouvy.com/training-calendar",
        @RequestHeader("Accept-Language") lang: String = "en-US,en;q=0.9",
        @RequestHeader("Accept-Encoding") enc: String = "gzip, deflate, br"
    ): String

    @GetMapping("/training-calendar")
    fun getCalendarBase(
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("User-Agent") ua: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
        @RequestHeader("Accept") accept: String = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
        @RequestHeader("Accept-Language") lang: String = "en-US,en;q=0.9"
    ): String
}

@FeignClient(name = "rouvy-activities", url = "https://activities.virtualtraining.eu")
interface RouvyActivitiesClient {

    @GetMapping("/export/activity_export_fit_{id}.fit")
    fun exportFit(
        @PathVariable("id") id: String,
        @RequestHeader("Cookie") cookie: String,
        @RequestHeader("User-Agent") ua: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
    ): ByteArray
}

//TODO("Not yet implemented")
@FeignClient(name = "rouvy-auth", url = "https://securetoken.googleapis.com")
interface RouvyAuthClient {
    @org.springframework.web.bind.annotation.PostMapping("/v1/token?key={apiKey}", consumes = ["application/x-www-form-urlencoded"])
    fun refreshToken(
        @PathVariable("apiKey") apiKey: String,
        @org.springframework.web.bind.annotation.RequestBody body: String
    ): Map<String, Any>
}

