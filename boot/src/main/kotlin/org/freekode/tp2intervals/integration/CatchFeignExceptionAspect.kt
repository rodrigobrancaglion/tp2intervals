package org.freekode.tp2intervals.integration

import feign.FeignException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.freekode.tp2intervals.config.log.AppLogger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component


@Aspect
@Component
class CatchFeignExceptionAspect {
     private val logger = AppLogger.get(this.javaClass)

    @Around("@annotation(org.freekode.tp2intervals.integration.CatchFeignException)")
    @Throws(Throwable::class)
    fun trace(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(CatchFeignException::class.java)
        val platform = annotation.platform
        try {
            return joinPoint.proceed()
        } catch (e: FeignException) {
            logger.warnL3In("HTTP request exception", e)

            var message = e.message ?: "Unknown error"
            if (e.status() == HttpStatus.FORBIDDEN.value() || e.status() == HttpStatus.UNAUTHORIZED.value()) {
                message = "Access denied"
            }
            throw PlatformException(platform, message)
        }
    }
}
