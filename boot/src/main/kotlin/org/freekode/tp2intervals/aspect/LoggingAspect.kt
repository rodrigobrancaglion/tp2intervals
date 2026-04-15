package org.freekode.tp2intervals.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Aspect handling logging for methods annotated with @Loggable.
 */
@Aspect
@Component
class LoggingAspect {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Around("@annotation(org.freekode.tp2intervals.aspect.LogService)")
    @Throws(Throwable::class)
    fun logExecutionService(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringType.getSimpleName()
        val methodName = signature.name
        val args = joinPoint.args

        // Get the logger associated with the original target class
        val targetLogger: Logger = LoggerFactory.getLogger(signature.declaringType)

        logger.info(
            ">>> Service | START - Method: {}.{}() called with arguments: {}",
            className,
            methodName,
            args.contentToString()
        )

        try {
            val result = joinPoint.proceed()
            targetLogger.info(
                "<<< Service | EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                className,
                methodName,
                result
            )
            return result
        } catch (throwable: Throwable) {
            targetLogger.error(
                "<<< Service | END (ERROR) - Method: {}.{}() with exception: {}",
                className,
                methodName,
                throwable.message
            )
            throw throwable
        }
    }

    @Around("@annotation(org.freekode.tp2intervals.aspect.LogRepository)")
    @Throws(Throwable::class)
    fun logExecutionRepository(joinPoint: ProceedingJoinPoint): Any? {

        val signature = joinPoint.signature as MethodSignature
        val className = signature.declaringType.getSimpleName()
        val methodName = signature.name
        val args = joinPoint.args
        val platform = try {
            val target = joinPoint.target
            // Check whether or not the platform() method is used
            val method = target.javaClass.getMethod("platform")
            method.invoke(target).toString()
        } catch (e: Exception) {
            "UNKNOWN"
        }

        // Get the logger associated with the original target class
        val targetLogger: Logger = LoggerFactory.getLogger(signature.declaringType)

        logger.info(
            ">>> Repository [{}] | Method: {}.{}() called with arguments: {}",
            platform,
            className,
            methodName,
            args.contentToString()
        )

        try {
            val result = joinPoint.proceed()
            targetLogger.info(
                "<<< Repository [{}] EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                platform,
                className,
                methodName,
                result
            )
            return result
        } catch (throwable: Throwable) {
            targetLogger.error(
                "<<< Repository [{}] END (ERROR) - Method: {}.{}() with exception: {}",
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
        val targetLogger: Logger = LoggerFactory.getLogger(signature.declaringType)

        logger.info(
            ">>> Schedule Job | START - Method: {}.{}()",
            className,
            methodName,
        )

        try {
            val result = joinPoint.proceed()
            targetLogger.info(
                "<<< Schedule Job | EXIT (SUCCESS) - Method: {}.{}(), with result: {}",
                className,
                methodName,
                result
            )
            return result
        } catch (throwable: Throwable) {
            targetLogger.error(
                "<<< Schedule Job | END (ERROR) - Method: {}.{}() with exception: {}",
                className,
                methodName,
                throwable.message
            )
            throw throwable
        }
    }
}