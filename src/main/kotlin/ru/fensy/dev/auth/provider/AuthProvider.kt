package ru.fensy.dev.auth.provider

import ru.fensy.dev.auth.domain.AuthResult

interface AuthProvider {

    fun name(): String

    suspend fun auth(accessToken: String): AuthResult

}
