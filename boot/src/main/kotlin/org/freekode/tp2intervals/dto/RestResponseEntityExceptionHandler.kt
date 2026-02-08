package org.freekode.tp2intervals.dto

import org.freekode.tp2intervals.integration.PlatformException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(PlatformException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun platformException(
        exception: PlatformException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(exception.platform.title, exception.message!!))
    }
}