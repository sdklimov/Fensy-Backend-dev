package ru.fensy.dev.chat.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime

@Table("user_presence")
data class PresenceEntity(
    @Id val userId: String,
    val online: Boolean,
    val lastSeen: OffsetDateTime,
)