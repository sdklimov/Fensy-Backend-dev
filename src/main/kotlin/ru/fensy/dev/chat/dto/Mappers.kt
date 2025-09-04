package ru.fensy.dev.chat.dto

import ru.fensy.dev.chat.model.MessageEntity
import ru.fensy.dev.chat.model.PresenceEntity

fun MessageEntity.toDto() = MessageDto(
    id = requireNotNull(id),
    senderId = senderId,
    recipientId = recipientId,
    content = if (deletedAt == null) content else null,
    replyToId = replyToId,
    createdAt = createdAt,
    deletedAt = deletedAt,
)

fun PresenceEntity.toDto() = PresenceDto(
    userId = userId,
    online = online,
    lastSeen = lastSeen,
)