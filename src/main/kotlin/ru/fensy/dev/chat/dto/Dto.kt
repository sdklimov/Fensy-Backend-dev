package ru.fensy.dev.chat.dto

import java.time.OffsetDateTime
import java.util.UUID

data class MessageDto(
    val id: UUID,
    val senderId: String,
    val recipientId: String,
    val content: String?,
    val replyToId: UUID?,
    val createdAt: OffsetDateTime,
    val deletedAt: OffsetDateTime?,
)

data class ChatSummaryDto(
    val peerId: String,
    val lastMessage: MessageDto?,
    val unreadCount: Int,
)

data class PresenceDto(
    val userId: String,
    val online: Boolean,
    val lastSeen: OffsetDateTime,
)