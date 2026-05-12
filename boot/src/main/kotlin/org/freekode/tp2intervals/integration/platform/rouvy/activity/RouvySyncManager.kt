package org.freekode.tp2intervals.integration.platform.rouvy.activity

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import com.microsoft.playwright.options.LoadState
import org.freekode.tp2intervals.integration.platform.rouvy.RouvyActivitiesClient
import org.freekode.tp2intervals.integration.platform.rouvy.activity.dto.RouvyActivityDTO
import org.freekode.tp2intervals.integration.platform.rouvy.configuration.RouvyConfigurationRepository
import org.freekode.tp2intervals.utils.Constants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import kotlin.use

@Service
class RouvySyncManager(
    private val activitiesClient: RouvyActivitiesClient,
    private val rouvyConfigurationRepository: RouvyConfigurationRepository,
    @Value("\${app.rouvy.account-url}") private val accountUrl: String,
    @Value("\${app.rouvy.api-url}") private val baseUrl: String
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

    fun getActivitiesInDateRange(startDate: LocalDate, endDate: LocalDate): List<RouvyActivityDTO> {
        val config = rouvyConfigurationRepository.getConfiguration()
        val email = config.email
        val password = config.password

        if (email.isBlank() || password.isBlank()) {
            log.error("Rouvy - credentials missing.")
            return emptyList()
        }

        val activitiesFound = mutableListOf<RouvyActivityDTO>()
        log.info("${Constants.logStringIndentation}Starting batch extraction from {} to {}", startDate, endDate)

        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(BrowserType.LaunchOptions().setHeadless(true))
            val context = browser.newContext(Browser.NewContextOptions().setUserAgent(USER_AGENT))
            val page = context.newPage()

            try {
                // 1. Autenticação
                log.info("${Constants.logStringIndentation}Navigating to login page")
                page.navigate("${accountUrl}/login", com.microsoft.playwright.Page.NavigateOptions().setTimeout(60000.0))
                page.waitForLoadState(LoadState.LOAD)

                log.info("${Constants.logStringIndentation}Performing authentication")
                performLoginInternal(page, email, password)

                // Wait for session to be established on account portal
                log.info("${Constants.logStringIndentation}Waiting for login confirmation")
                page.waitForURL({ url -> url.contains("account.rouvy.com") && !url.contains("/login") },
                    com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(60000.0))

                // 2. Acessar lista de atividades
                log.info("${Constants.logStringIndentation}Transitioning to Riders Portal")
                page.navigate("${baseUrl}/profile/overview")
                page.waitForLoadState(LoadState.DOMCONTENTLOADED)
                page.waitForTimeout(3000.0)

                // 3. Mapear IDs das atividades no intervalo
                val activityLinks = page.locator("div.flex.flex-col.gap-y-2 > a[href*='/activity/']")
                val count = activityLinks.count()
                val targetActivitiesMap = mutableMapOf<String, LocalDate>()

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
                                    targetActivitiesMap[id] = activityDate
                                }
                            }
                        } catch (e: Exception) {
                            log.error("${Constants.logStringIndentation}Erro ao processar data no loop inicial: ${e.message}")
                        }
                    }
                }

                // 4. Processar cada atividade encontrada
                // CAMPO - 1 / 6 | ID e ActivityDate
                for ((id, activityDate) in targetActivitiesMap) {
                    try {
                        log.info("${Constants.logStringIndentation}Processing activity: {} from date: {}", id, activityDate)
                        page.navigate("${baseUrl}/activity/$id")
                        page.waitForLoadState(LoadState.LOAD)

                        // CAMPO - 2 | Título da Atividade (ex: "Morning Ride")
                        val activityTitle = page.locator("h1, h2").first().textContent().trim()

                        // CAMPO - 3 | TOKEN
                        val sessionCookie = context.cookies().find { it.name == "rouvy_session" }
                        val token = sessionCookie?.let { URLDecoder.decode(it.value, StandardCharsets.UTF_8) } ?: ""

                        // CAMPO - 4 | Download do FIT
                        val download = page.waitForDownload {
                            page.locator("button:has-text('Export'), a:has-text('FIT'), [class*='download']").first().click()
                        }
                        val fitBytes = download.createReadStream().use { it.readAllBytes() }
                        //val fitBytes = download(id, activityTitle, token)

                        // CAMPO - 5 | Nome da Rota
                        var finalRouteName = activityTitle // fallback

                        // Pega os dados do link da Rota para navegar na pagina de Rotas
                        val routeDetailLink = page.locator("a[data-cy='route-detail']").first()
                        if (routeDetailLink.count() > 0) {
                            val routeHref = routeDetailLink.getAttribute("href")
                            if (!routeHref.isNullOrBlank()) {
                                log.info("${Constants.logStringIndentation}Navigating to route detail: {}", routeHref)
                                // Navega para a página da rota (ex: /route/291336)
                                page.navigate("${baseUrl}$routeHref")
                                page.waitForLoadState(LoadState.LOAD)

                                // Extrai o og:title da meta tag
                                val ogTitle = page.locator("meta[property='og:title']").getAttribute("content")
                                if (!ogTitle.isNullOrBlank()) {
                                    // Dividimos a string em uma lista usando o separador "|"
                                    val parts = ogTitle.split("|").map { it.trim() }

                                    // Se a última parte for "ROUVY" (case insensitive), removemos ela
                                    if (parts.isNotEmpty() && parts.last().equals("ROUVY", ignoreCase = true)) {
                                        // Juntamos todas as partes novamente, exceto a última
                                        finalRouteName = parts.dropLast(1).joinToString(" | ")
                                    } else {
                                        finalRouteName = ogTitle
                                    }
                                }

                                // Volta para a página da atividade para baixar o FIT
//                                page.navigate("${baseUrl}/activity/$id")
//                                page.waitForLoadState(LoadState.LOAD)
                            }
                        }

                        // Adiciona à lista com a data correta e o nome da rota limpo
                        activitiesFound.add(
                            RouvyActivityDTO(
                                id,
                                activityTitle,
                                token,
                                fitBytes,
                                finalRouteName,
                                activityDate
                            )
                        )

                    } catch (e: Exception) {
                        log.error("${Constants.logStringIndentation}Failed to process activity $id: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                log.error("${Constants.logStringIndentation}Batch extraction failed: {}", e.message)
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

    private fun download(id: String, name: String, cookie: String): ByteArray? {
        return try {
            log.info("${Constants.logStringIndentation}Baixando FIT para atividade $id ($name)...")
            val rawCookie = "rouvy_session=$cookie"
            
            val actualFitBytes = activitiesClient.exportFit(id, rawCookie)

            if (actualFitBytes.isNotEmpty()) {
                log.info("${Constants.logStringIndentation}Sucesso! FIT extraído para $id (${actualFitBytes.size} bytes).")
                actualFitBytes
            } else {
                log.warn("${Constants.logStringIndentation}Arquivo FIT retornado vazio para $id")
                null
            }
        } catch (e: Exception) {
            log.warn("${Constants.logStringIndentation}Não foi possível baixar o FIT $id: ${e.message}")
            null
        }
    }
}