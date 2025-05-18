package ru.fensy.dev.domain

import java.time.OffsetDateTime

data class UserSettings(
    val id: Long,
    val userId: Long,
    val allowMessagesFrom: AllowMessagesFrom,
    val notificationsOnEmail: Boolean,
    val adOnEmail: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)