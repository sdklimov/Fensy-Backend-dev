package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

data class RefreshToken(
    val id: UUID? = null,
    val user: User,
    val tokenHash: String,
    val jwtId: String,
    val expiresAt: OffsetDateTime,
    val revoked: Boolean,
)
