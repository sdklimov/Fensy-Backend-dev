package ru.fensy.dev.exceptionhandler

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Hidden
class RestExceptionHandler {
    private val logger = KotlinLogging.logger {}

    @ExceptionHandler
    fun handleException(ex: Exception): String {
        logger.error(ex) { "Unhandled exception in REST controller" }
        return "Error";
    }
}