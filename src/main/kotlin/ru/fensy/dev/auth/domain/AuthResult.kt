package ru.fensy.dev.auth.domain

data class AuthResult(
    val isUserCreated: Boolean,
    val bearerToken: String,
)