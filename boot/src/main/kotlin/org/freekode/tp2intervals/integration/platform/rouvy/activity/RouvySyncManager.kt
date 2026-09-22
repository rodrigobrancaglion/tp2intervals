package org.freekode.tp2intervals.integration.platform.rouvy.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.integration.platform.rouvy.RouvyRidersClient
import org.freekode.tp2intervals.integration.platform.rouvy.activity.dto.RouvyActivityDTO
import org.freekode.tp2intervals.integration.platform.rouvy.configuration.RouvyConfigurationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.use

@Service
class RouvySyncManager(
    private val ridersClient: RouvyRidersClient,
    private val rouvyConfigurationRepository: RouvyConfigurationRepository,
    @Value("\${app.rouvy.account-url}") private val accountUrl: String,
    @Value("\${app.rouvy.api-url}") private val baseUrl: String
) {
    private val logger = AppLogger.get(this.javaClass)

    private val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

    // Data class to hold activity details fetched via API
    data class ActivityInfo(
        val date: LocalDate,
        val fitUrl: String?,
        val routeId: String?
    )

    fun getActivitiesInDateRange(startDate: LocalDate, endDate: LocalDate): List<RouvyActivityDTO> {
        val config = rouvyConfigurationRepository.getConfiguration()
        val email = config.email
        val password = config.password

        if (email.isBlank() || password.isBlank()) {
            logger.errorL2In("Rouvy - credentials missing.")
            return emptyList()
        }

        val activitiesFound = mutableListOf<RouvyActivityDTO>()
        logger.infoL3In("Starting batch extraction from {} to {}", startDate, endDate)

        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true))
            val context = browser.newContext(Browser.NewContextOptions().setUserAgent(USER_AGENT))
            val page = context.newPage()

            try {
                // 1. Authentication
                logger.infoL3In("Navigating to login page")
                page.navigate("${accountUrl}/login", com.microsoft.playwright.Page.NavigateOptions().setTimeout(60000.0))
                page.waitForLoadState(LoadState.LOAD)

                logger.infoL3In("Performing authentication")
                performLoginInternal(page, email, password)

                // Wait for session to be established after login. Rouvy may redirect to account or riders domain.
                logger.infoL3In("Waiting for login confirmation")
                // Dismiss possible cookie consent banner if present
                // Dismiss possible cookie consent banner if present (Italian)
                try {
                    // Italian button "Accetta tutti"
                    page.locator("#CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll").first().click()
                } catch (e: Exception) {
                    // ignore if not found
                }
                page.waitForURL({ url ->
                    (url.contains("account.rouvy.com") || url.contains("riders.rouvy.com")) &&
                    !url.contains("/login") && !url.contains("/reset")
                }, com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(120000.0))
                logger.infoL3In("Logged in, current URL: {}", page.url())
                logger.infoL3In("Transitioning to Riders Portal")
                page.navigate("${baseUrl}/profile/overview")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)
                page.waitForTimeout(3000.0)

                // 3. Map activity IDs within date range
                // A. Fetch activities via API (including FIT URL and routeId)
                val allCookies = context.cookies().joinToString("; ") { "${it.name}=${it.value}" }
                val targetActivitiesMap = mutableMapOf<String, ActivityInfo>()
                try {
                    val apiMap = fetchActivitiesViaApi(startDate, endDate, allCookies)
                    targetActivitiesMap.putAll(apiMap)
                } catch (e: Exception) {
                    logger.warnL3In("API fetch failed: {}. Falling back to UI scraping.", e.message)
                }

                // B. Fallback UI scraping if API returned empty
                if (targetActivitiesMap.isEmpty()) {
                    logger.infoL3In("API returned no activities, falling back to UI scraping.")
                    val activityLinks = page.locator("div.flex.flex-col.gap-y-2 > a[href*='/activity/']")
                    val count = activityLinks.count()
                    val dateRegex = Regex("""(?i)([a-z]{3}\s\d{2},\s\d{4})""")
                    val formatter = java.time.format.DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("MMM dd, yyyy")
                        .toFormatter(java.util.Locale.ENGLISH)

                    for (i in 0 until count) {
                        val link = activityLinks.nth(i)
                        val innerText = link.innerText()
                        val dateMatch = dateRegex.find(innerText)

                        if (dateMatch != null) {
                            try {
                                val activityDate = LocalDate.parse(dateMatch.value, formatter)

                                if (!activityDate.isBefore(startDate) && !activityDate.isAfter(endDate)) {
                                    val href = link.getAttribute("href") ?: ""
                                    val id = href.split("/").lastOrNull()
                                    if (id != null) {
                                        // UI fallback only provides date; other fields are unknown
                                        targetActivitiesMap[id] = ActivityInfo(activityDate, null, null)
                                    }
                                }
                            } catch (e: Exception) {
                                logger.errorL3In("Error processing date in initial loop: ${e.message}")
                            }
                        }
                    }
                }

                // 4. Process each activity found (using API data when available)
                for ((id, info) in targetActivitiesMap) {
                    try {
                        logger.infoL3In("Processing activity: {} from date: {}", id, info.date)
                        // If we have a direct FIT URL, download it without navigating to the activity page
                        val fitBytes: ByteArray? = if (info.fitUrl != null) {
                            logger.infoL3In("Downloading FIT via direct URL for activity {}", id)
                            downloadFitFromUrl(info.fitUrl)
                        } else {
                            // Fallback to UI download (existing logic)
                            page.navigate("${baseUrl}/activity/$id")
                            page.waitForLoadState(LoadState.LOAD)
                            val download = page.waitForDownload {
                                page.locator("button:has-text('Export'), a:has-text('FIT'), [class*='download']").first().click()
                            }
                            download.createReadStream().use { it.readAllBytes() }
                        }

                        // Guard against missing FIT
                        if (fitBytes == null) {
                            logger.warnL3In("FIT not available for activity {}", id)
                            continue
                        }

                        // Title extraction (still via UI, as route name may depend on routeId)
                        page.navigate("${baseUrl}/activity/$id")
                        page.waitForLoadState(LoadState.LOAD)
                        val activityTitle = page.locator("h1, h2").first().textContent().trim()

                        // Route name: try to fetch via API using routeId if present, otherwise fallback UI
                        var finalRouteName = activityTitle // fallback
                        if (info.routeId != null) {
                            val routeName = fetchRouteNameViaApi(info.routeId)
                            if (routeName != null) {
                                finalRouteName = routeName
                            }
                        } else {
                            // UI fallback (existing logic)
                            val routeDetailLink = page.locator("a[data-cy='route-detail']").first()
                            if (routeDetailLink.count() > 0) {
                                val routeHref = routeDetailLink.getAttribute("href")
                                if (!routeHref.isNullOrBlank()) {
                                    logger.infoL3In("Navigating to route detail: {}", routeHref)
                                    page.navigate("${baseUrl}$routeHref")
                                    page.waitForLoadState(LoadState.LOAD)
                                    val ogTitle = page.locator("meta[property='og:title']").getAttribute("content")
                                    if (!ogTitle.isNullOrBlank()) {
                                        val parts = ogTitle.split("|").map { it.trim() }
                                        finalRouteName = if (parts.isNotEmpty() && parts.last().equals("ROUVY", ignoreCase = true)) {
                                            parts.dropLast(1).joinToString(" | ")
                                        } else {
                                            ogTitle
                                        }
                                    }
                                }
                            }
                        }

                        activitiesFound.add(
                            RouvyActivityDTO(
                                id,
                                activityTitle,
                                "", // token not needed for FIT when using direct URL
                                fitBytes,
                                finalRouteName,
                                info.date
                            )
                        )
                        logger.infoL3In("Processed Activity: Title: {}, Route Name: {}", activityTitle, finalRouteName)
                    } catch (e: Exception) {
                        logger.errorL3In("Failed to process activity $id: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                logger.errorL3In("Batch extraction failed: {}", e.message)
            } finally {
                browser.close()
            }
        }

        return activitiesFound
    }

    private fun performLoginInternal(page: com.microsoft.playwright.Page, email: String, password: String) {
        page.waitForSelector("input[name=\"email\"]", com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(30000.0))
        page.fill("input[name=\"email\"]", email)
        page.fill("input[name=\"password\"]", password)
        page.waitForTimeout(500.0)
        page.keyboard().press("Enter")
    }

    // Helper method to download FIT file from a direct URL using RestTemplate
    private fun downloadFitFromUrl(url: String): ByteArray? {
        return try {
            val rest = RestTemplate()
            val bytes = rest.getForObject(url, ByteArray::class.java)
            bytes
        } catch (e: Exception) {
            logger.warnL3In("Failed to download FIT from URL $url: ${e.message}")
            null
        }
    }

    // Helper method to retrieve route name by fetching the route's HTML page and extracting og:image:alt
    private fun fetchRouteNameViaApi(routeId: String): String? {
        return try {
            val body = ridersClient.getRoutePage(routeId)
            if (body.isNullOrBlank()) {
                null
            } else {
                // Extract the og:image:alt meta tag content using Regex
                val regex = Regex("""<meta\s+property="og:image:alt"\s+content="([^"]+)"""")
                val matchResult = regex.find(body)
                matchResult?.groupValues?.get(1)
            }
        } catch (e: Exception) {
            logger.warnL3In("Exception while fetching route name for routeId {}: {}", routeId, e.message)
            null
        }
    }

    // ---------------------------------------------------------------------
    // Backup version of fetchActivitiesViaApi (original implementation) kept for reference.
    // ---------------------------------------------------------------------
    @Deprecated("Legacy API fetch - only returns activity date")
    private fun fetchActivitiesViaApiLegacy(context: com.microsoft.playwright.BrowserContext, startDate: LocalDate, endDate: LocalDate, sessionCookie: String): Map<String, ActivityInfo> {
        val base = "https://riders.rouvy.com/resources/activities-pagination.data"
        val result = mutableMapOf<String, ActivityInfo>()
        var offset = 0
        val mapper = ObjectMapper()
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        while (true) {
            val url = "$base?offset=$offset&routes=routes%2Fresources.activities-pagination"
            val rest = RestTemplate()
            val headers = HttpHeaders()
            headers.add("Cookie", "rouvy_session=$sessionCookie")
            val entity = org.springframework.http.HttpEntity<String>(headers)
            val response = rest.exchange(url, org.springframework.http.HttpMethod.GET, entity, String::class.java)
            if (!response.statusCode.is2xxSuccessful) {
                logger.warnL3In("Failed to fetch activities page offset $offset: {}", response.statusCode)
                break
            }
            val json = response.body ?: break
            val node = mapper.readTree(json)
            if (!node.isArray) break
            var foundAny = false
            for (item in node) {
                if (item.isObject) {
                    val id = item.get("id")?.asText()
                    val startUtc = item.get("startUTC")?.asText()
                    if (id != null && startUtc != null) {
                        val activityDate = OffsetDateTime.parse(startUtc, formatter).toLocalDate()
                        if (!activityDate.isBefore(startDate) && !activityDate.isAfter(endDate)) {
                            // Legacy version only stores date, other fields null
                            result[id] = ActivityInfo(activityDate, null, null)
                            foundAny = true
                        }
                    }
                }
            }
            if (!foundAny) break
            offset += 1
        }
        return result
    }

    // ---------------------------------------------------------------------
    // New implementation that extracts FIT URL and routeId from the API response.
    // ---------------------------------------------------------------------
    private fun fetchActivitiesViaApi(startDate: LocalDate, endDate: LocalDate, allCookies: String): Map<String, ActivityInfo> {
        val result = mutableMapOf<String, ActivityInfo>()
        var offset = 0
        val mapper = ObjectMapper()
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        while (true) {
            val json = try {
                ridersClient.getActivitiesPagination(offset, allCookies)
            } catch (e: Exception) {
                logger.warnL3In("Failed to fetch activities page offset $offset via Feign: {}", e.message)
                break
            }
            if (json.isNullOrBlank()) {
                logger.warnL3In("API response is empty.")
                break
            }
            
            val node = mapper.readTree(json)
            if (!node.isArray) {
                logger.warnL3In("API response is not an array. Root node type: {}", node.nodeType)
                break
            }
            
            // Remix/Turbo JSON graph format: flat array of values and pointer objects {"_K": V}
            val stringIndices = mutableMapOf<String, Int>()
            for (i in 0 until node.size()) {
                val elem = node.get(i)
                if (elem.isTextual) {
                    stringIndices.putIfAbsent(elem.asText(), i)
                }
            }
            
            val idKey = stringIndices["id"]
            val startUtcKey = stringIndices["startUTC"]
            val attachmentsKey = stringIndices["attachments"]
            val typeKey = stringIndices["type"]
            val urlKey = stringIndices["url"]
            val routeIdKey = stringIndices["routeId"]
            
            var foundAny = false
            for (i in 0 until node.size()) {
                val item = node.get(i)
                if (item.isObject) {
                    val idValIndex = idKey?.let { item.get("_$it")?.asInt() }
                    val startUtcValIndex = startUtcKey?.let { item.get("_$it")?.asInt() }
                    
                    if (idValIndex != null && idValIndex >= 0 && startUtcValIndex != null && startUtcValIndex >= 0) {
                        val id = node.get(idValIndex)?.asText()
                        val startUtc = node.get(startUtcValIndex)?.asText()
                        
                        if (id != null && startUtc != null) {
                            try {
                                val activityDate = OffsetDateTime.parse(startUtc, formatter).toLocalDate()
                                if (!activityDate.isBefore(startDate) && !activityDate.isAfter(endDate)) {
                                    val routeIdValIndex = routeIdKey?.let { item.get("_$it")?.asInt() }
                                    val routeId = routeIdValIndex?.let { if (it >= 0) node.get(it)?.asText() else null }
                                    
                                    var fitUrl: String? = null
                                    val attachmentsListIndex = attachmentsKey?.let { item.get("_$it")?.asInt() }
                                    if (attachmentsListIndex != null && attachmentsListIndex >= 0) {
                                        val attachmentsList = node.get(attachmentsListIndex)
                                        if (attachmentsList != null && attachmentsList.isArray) {
                                            for (attPtr in attachmentsList) {
                                                val attObj = node.get(attPtr.asInt())
                                                if (attObj != null && attObj.isObject) {
                                                    val typeIndex = typeKey?.let { attObj.get("_$it")?.asInt() }
                                                    val urlIndex = urlKey?.let { attObj.get("_$it")?.asInt() }
                                                    
                                                    val typeStr = typeIndex?.let { if (it >= 0) node.get(it)?.asText() else null }
                                                    if (typeStr == "FIT_FILE") {
                                                        fitUrl = urlIndex?.let { if (it >= 0) node.get(it)?.asText() else null }
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    result[id] = ActivityInfo(activityDate, fitUrl, routeId)
                                    foundAny = true
                                }
                            } catch (e: Exception) {
                                // Not a valid date format, ignore
                            }
                        }
                    }
                }
            }
            
            if (!foundAny) break
            offset += 1
        }
        return result
    }
}