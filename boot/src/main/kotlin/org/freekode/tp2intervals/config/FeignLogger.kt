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

        // Dynamically instantiate AppLogger with the Feign client interface
        val targetLogger = getTargetLogger(configKey)
        val methodName = extractMethodName(configKey)
        val httpMethod = request.httpMethod().name
        val url = request.url()

        val contentType = request.headers()["Content-Type"]?.firstOrNull()
            ?: request.headers()["content-type"]?.firstOrNull()
            ?: ""

        val isMultipartOrBinary = isMultipartOrBinary(contentType)

        val bodyBytes = request.body()
        val bodyFormatted = if (isMultipartOrBinary) {
            "[MULTIPART / BINARY BODY SUPPRESSED]"
        } else if (bodyBytes != null && bodyBytes.isNotEmpty()) {
            val requestJson = String(bodyBytes, StandardCharsets.UTF_8)
            if (isValidJsonString(requestJson)) {
                sanitizeJson(requestJson)
            } else if (isPrintableText(requestJson)) {
                requestJson
            } else {
                "[BINARY BODY SUPPRESSED]"
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

        // Dynamically instantiate AppLogger with the Feign client interface
        val targetLogger = getTargetLogger(configKey)
        val methodName = extractMethodName(configKey)
        val httpMethod = response.request().httpMethod().name
        val status = response.status()

        val contentType = response.headers()["content-type"]?.firstOrNull()
            ?: response.headers()["Content-Type"]?.firstOrNull()
            ?: ""

        val isJsonContentType = contentType.contains("application/json", ignoreCase = true)
        val isMultipartOrBinary = isMultipartOrBinary(contentType)

        var bodyFormatted = if (isMultipartOrBinary) "[BINARY BODY SUPPRESSED]" else "[NO BODY]"
        var rebufferedResponse = response

        if (isJsonContentType) {
            val body = response.body()
            if (body != null) {
                val bodyData = Util.toByteArray(body.asInputStream())
                if (bodyData.isNotEmpty()) {
                    val responseJson = String(bodyData, StandardCharsets.UTF_8)
                    if (isValidJsonString(responseJson)) {
                        bodyFormatted = sanitizeJson(responseJson)
                    } else if (isPrintableText(responseJson)) {
                        bodyFormatted = responseJson
                    } else {
                        bodyFormatted = "[BINARY BODY SUPPRESSED]"
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
     * Extracts only the clean method name.
     * Transforms "org.freekode...IcuWorkoutApiClient#getEvents(String,...)" into "getEvents"
     */
    private fun extractMethodName(configKey: String): String {
        return configKey.substringBefore("(").substringAfterLast("#")
    }

    /**
     * Extracts the class/interface from configKey and instantiates AppLogger.
     * Works whether configKey contains the full package or just the simple name.
     */
    private fun getTargetLogger(configKey: String): AppLogger {
        val simpleClassName = configKey.substringBefore("#")

        // Creates the SLF4J logger with the target class name
        val slf4jLogger = LoggerFactory.getLogger(simpleClassName)

        // Wraps the Logger instance into AppLogger
        return AppLogger.get(slf4jLogger)
    }

    /**
     * Checks if the called method should be suppressed from logs.
     */
    private fun isIgnoredMethod(configKey: String): Boolean {
        val methodName = configKey.substringBefore("(").substringAfterLast("#")
        return methodName.contains("Token", ignoreCase = true)
    }

    /**
     * Removes or replaces the "data" field if present at the root of JSON.
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
     * Checks if trimmed string starts with '{' or '[' (JSON standard).
     */
    private fun isValidJsonString(content: String): Boolean {
        val trimmed = content.trim()
        return trimmed.startsWith("{") || trimmed.startsWith("[")
    }

    /**
     * Checks if Content-Type is multipart or binary.
     */
    private fun isMultipartOrBinary(contentType: String): Boolean {
        val lower = contentType.lowercase()
        return lower.contains("multipart/") ||
                lower.contains("application/octet-stream") ||
                lower.contains("application/zip") ||
                lower.contains("application/x-") ||
                lower.contains("image/") ||
                lower.contains("audio/") ||
                lower.contains("video/")
    }

    /**
     * Checks if the text consists mostly of printable characters.
     */
    private fun isPrintableText(text: String): Boolean {
        if (text.isEmpty()) return true
        val nonPrintableCount = text.count { it < 0x20.toChar() && it != '\t' && it != '\r' && it != '\n' }
        return (nonPrintableCount.toDouble() / text.length) < 0.05
    }

    override fun log(configKey: String, format: String, vararg args: Any) {
        // Empty to suppress default Feign headers and metadata
    }
}