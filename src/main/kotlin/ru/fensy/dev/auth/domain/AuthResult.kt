package ru.fensy.dev.auth.domain

import ru.fensy.dev.domain.User

data class AuthResult(
    val isUserCreated: Boolean,
    val user: User,
)