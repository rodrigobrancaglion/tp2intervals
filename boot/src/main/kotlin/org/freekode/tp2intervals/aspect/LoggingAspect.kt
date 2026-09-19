package org.freekode.tp2intervals.aspect

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.freekode.tp2intervals.config.log.AppLogger
import org.freekode.tp2intervals.domain.Platform
import org.springframework.stereotype.Component
import java.time.temporal.Temporal

/**
 * Aspect handling logging for methods annotated with @Loggable.
 */
@Aspect
@Component
class LoggingAspect {

    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    @Around("@annotation(org.freekode.tp2intervals.aspect.LogService)")
    @Throws(Throwable::class)
    fun logExecutionService(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringType.getSimpleName()
        val methodName = signature.name
        val parameterNames = signature.parameterNames
        val args = joinPoint.args

        // Extract sourcePlatform and targetPlatform if available
        val platformInfo = extractPlatformInfo(args)

        // Get the logger associated with the original target class
        val logger = AppLogger.get(signature.declaringType)

        val logMessage = if (platformInfo.isNotEmpty()) {
            "Service | START - Method: {}.{}() called $platformInfo with arguments: {}"
        } else {
            "Service | START - Method: {}.{}() called with arguments: {}"
        }

        logger.infoL1In(logMessage, className, methodName, formatArgsWithNames(parameterNames, args))

        try {
            val result = joinPoint.proceed()
            logger.infoL1Out(
                "Service | EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                className,
                methodName,
                formatResult(result)
            )
            return result
        } catch (throwable: Throwable) {
            logger.errorL1Out(
                "Service | END (ERROR) - Method: {}.{}() with exception: {}",
                className,
                methodName,
                throwable.message
            )
            throw throwable
        }
    }

    @Around("@within(org.springframework.stereotype.Repository) || @annotation(org.freekode.tp2intervals.aspect.LogRepository)")
    @Throws(Throwable::class)
    fun logExecutionRepository(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val returnType = signature.returnType
        val methodName = signature.name
        val parameterNames = signature.parameterNames

        // Ignora a execução do metodo platform() para evitar poluicao visual nos logs
        if (methodName == "platform") {
            return joinPoint.proceed()
        }

        val className = signature.declaringType.getSimpleName()
        val args = joinPoint.args
        val platform = try {
            val target = joinPoint.target
            // Check whether or not the platform() method is used
            val method = target.javaClass.getMethod("platform")
            method.invoke(target).toString()
        } catch (e: Exception) {
            ""
        }

        // Get the logger associated with the original target class
        val logger = AppLogger.get(signature.declaringType)

        val logMessage = if (args.isNotEmpty()) {
            "Repository [{}] | START - Method: {}.{}() called with arguments: {}"
        } else {
            "Repository [{}] | START - Method: {}.{}() called without arguments{}"
        }

        logger.infoL2In(
            logMessage,
            platform,
            className,
            methodName,
            formatArgsWithNames(parameterNames, args)
        )

        try {
            val result = joinPoint.proceed()
            // Verifica se o tipo de retorno é void
            val isVoid = returnType == Void.TYPE || returnType.name.equals("void", ignoreCase = true)

            if (isVoid) {
                logger.infoL2Out("Repository [{}] | EXIT (SUCCESS) - Method: {}.{}() void",
                    platform,
                    className,
                    methodName)
            } else {
                logger.infoL2Out("Repository [{}] | EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                    platform,
                    className,
                    methodName,
                    formatResult(result))
            }

            return result
        } catch (throwable: Throwable) {
            logger.errorL2Out(
                "Repository [{}] | END (ERROR) - Method: {}.{}() with exception: {}",
                platform,
                className,
                methodName,
                throwable.message
            )
            throw throwable
        }
    }

    @Around("@annotation(org.freekode.tp2intervals.aspect.LogJob)")
    @Throws(Throwable::class)
    fun logExecutionJob(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringType.getSimpleName()
        val methodName = signature.name

        // Get the logger associated with the original target class
        val logger = AppLogger.get(signature.declaringType)

        logger.infoL1In(
            "Schedule Job | START - Method: {}.{}()",
            className,
            methodName,
        )

        try {
            val result = joinPoint.proceed()
            logger.infoL1Out(
                "Schedule Job | EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                className,
                methodName,
                formatResult(result)
            )
            return result
        } catch (throwable: Throwable) {
            logger.errorL1Out(
                "Schedule Job | END (ERROR) - Method: {}.{}() with exception: {}",
                className,
                methodName,
                throwable.message
            )
            throw throwable
        }
    }

    private fun formatArgsWithNames(parameterNames: Array<String>?, args: Array<Any?>): String {
        if (args.isEmpty()) return "[]"

        if (parameterNames == null || parameterNames.size != args.size) {
            return try {
                objectMapper.writeValueAsString(args)
            } catch (e: Exception) {
                args.contentToString()
            }
        }

        val formattedParams = parameterNames.zip(args).joinToString(", ") { (name, value) ->
            val formattedValue = when (value) {
                null -> "null"
                is Temporal -> {
                    // Serializa como JSON e substitui os colchetes [2026,8,21] por chaves {2026,8,21}
                    val rawJson = objectMapper.writeValueAsString(value)
                    rawJson.replace('[', '{').replace(']', '}')
                }
                else -> {
                    try {
                        objectMapper.writeValueAsString(value)
                    } catch (e: Exception) {
                        value.toString()
                    }
                }
            }
            "$name: $formattedValue"
        }

        return "[$formattedParams]"
    }

    /**
     * Converte o resultado de retorno para JSON de forma segura.
     */
    private fun formatResult(result: Any?): String {
        if (result == null) return "null"
        return try {
            objectMapper.writeValueAsString(result)
        } catch (e: Exception) {
            result.toString()
        }
    }


    private fun extractPlatformInfo(args: Array<Any?>): String {
        // 1. Direct Platform arguments
        val directPlatforms = args.filterIsInstance<Platform>()
        if (directPlatforms.size == 2) {
            return "from ${directPlatforms[0]} to ${directPlatforms[1]}"
        }
        if (directPlatforms.size == 1) {
            return "for ${directPlatforms[0]}"
        }

        // 2. Look inside request objects (e.g. CopyC2CRequest)
        for (arg in args) {
            if (arg == null || arg is Platform || arg is String || arg is Number || arg is Boolean) continue

            try {
                val source = try {
                    arg.javaClass.getMethod("getSourcePlatform").invoke(arg) as? Platform
                } catch (e: Exception) {
                    null
                }
                val target = try {
                    arg.javaClass.getMethod("getTargetPlatform").invoke(arg) as? Platform
                } catch (e: Exception) {
                    null
                }

                if (source != null && target != null) {
                    return "from $source to $target"
                }
                if (source != null) return "from $source"
                if (target != null) return "to $target"
            } catch (e: Exception) {
                // Ignore reflection errors for this argument
            }
        }

        return ""
    }
}