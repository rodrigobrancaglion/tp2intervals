package org.freekode.tp2intervals.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import feign.Logger
import feign.Request
import feign.Response
import feign.Util
import org.freekode.tp2intervals.config.log.AppLogger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.StandardCharsets

class FeignLogger : Logger() {

    private val objectMapper = ObjectMapper()

    override fun logRequest(configKey: String, level: Level, request: Request) {
        if (isIgnoredMethod(configKey)) return

        // Instancia o AppLogger dinamicamente com a interface do cliente Feign
        val targetLogger = getTargetLogger(configKey)
        val methodName = extractMethodName(configKey)
        val httpMethod = request.httpMethod().name
        val url = request.url()

        val bodyBytes = request.body()
        val bodyFormatted = if (bodyBytes != null && bodyBytes.isNotEmpty()) {
            val requestJson = String(bodyBytes, StandardCharsets.UTF_8)
            if (isValidJsonString(requestJson)) {
                sanitizeJson(requestJson)
            } else {
                requestJson
            }
        } else {
            "[NO BODY]"
        }

        targetLogger.infoL4In(
            "REQUEST  | Method: {}() | [{}] URL: {} - BODY: {}",
            methodName,
            httpMethod,
            url,
            bodyFormatted
        )
    }

    @Throws(IOException::class)
    override fun logAndRebufferResponse(
        configKey: String,
        logLevel: Level,
        response: Response,
        elapsedTime: Long
    ): Response {
        if (isIgnoredMethod(configKey)) return response

        // Instancia o AppLogger dinamicamente com a interface do cliente Feign
        val targetLogger = getTargetLogger(configKey)
        val methodName = extractMethodName(configKey)
        val httpMethod = response.request().httpMethod().name
        val status = response.status()

        val contentType = response.headers()["content-type"]?.firstOrNull() ?: ""
        val isJsonContentType = contentType.contains("application/json", ignoreCase = true)

        var bodyFormatted = "[NO BODY]"
        var rebufferedResponse = response

        if (isJsonContentType) {
            val body = response.body()
            if (body != null) {
                val bodyData = Util.toByteArray(body.asInputStream())
                if (bodyData.isNotEmpty()) {
                    val responseJson = String(bodyData, StandardCharsets.UTF_8)
                    if (isValidJsonString(responseJson)) {
                        bodyFormatted = sanitizeJson(responseJson)
                    } else {
                        bodyFormatted = responseJson
                    }
                }
                rebufferedResponse = response.toBuilder().body(bodyData).build()
            }
        }

        targetLogger.infoL4In(
            "RESPONSE | Method: {}() | STATUS: {} | BODY: {}",
            methodName,
            status,
            bodyFormatted
        )

        return rebufferedResponse
    }

    /**
     * Extrai apenas o nome do metodo limpo.
     * Transforma "org.freekode...IcuWorkoutApiClient#getEvents(String,...)" em "getEvents"
     */
    private fun extractMethodName(configKey: String): String {
        return configKey.substringBefore("(").substringAfterLast("#")
    }

    /**
     * Extrai a classe/interface da configKey e instancia o AppLogger.
     * Funciona tanto se a configKey contiver o pacote completo quanto se contiver apenas o nome simples.
     */
    private fun getTargetLogger(configKey: String): AppLogger {
        val simpleClassName = configKey.substringBefore("#")

        // Cria o Logger do SLF4J passando o nome simples ("TrainingPeaksUserApiClient")
        val slf4jLogger = LoggerFactory.getLogger(simpleClassName)

        // Passa a instância do Logger para o AppLogger
        return AppLogger.get(slf4jLogger)
    }

    /**
     * Verifica se o metodo chamado deve ser ignorado nos logs.
     */
    private fun isIgnoredMethod(configKey: String): Boolean {
        val methodName = configKey.substringBefore("(").substringAfterLast("#")
        return methodName.contains("Token", ignoreCase = true)
    }

    /**
     * Remove ou substitui o campo "data" caso esteja presente na raiz do JSON.
     */
    private fun sanitizeJson(jsonString: String): String {
        return try {
            val tree = objectMapper.readTree(jsonString)
            if (tree.isObject) {
                val objectNode = tree as ObjectNode
                if (objectNode.has("data")) {
                    objectNode.put("data", "[BYTE_DATA_SUPPRESSED]")
                }
            }
            objectMapper.writeValueAsString(tree)
        } catch (e: Exception) {
            jsonString
        }
    }

    /**
     * Verifica se a string limpa começa com '{' ou '[' (padrão JSON).
     */
    private fun isValidJsonString(content: String): Boolean {
        val trimmed = content.trim()
        return trimmed.startsWith("{") || trimmed.startsWith("[")
    }

    override fun log(configKey: String, format: String, vararg args: Any) {
        // Vazio para suprimir cabeçalhos e metadados padrao
    }
}