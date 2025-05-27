package ru.fensy.dev.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UserNotFoundException(message: String): RuntimeException(message)