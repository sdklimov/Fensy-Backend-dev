package ru.fensy.dev.chat.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.UUID

@Table("messages")
data class MessageEntity(
    @Id val id: UUID? = null,
    @Column("sender_id") val senderId: String,
    @Column("recipient_id") val recipientId: String,
    val content: String,
    @Column("reply_to_id") val replyToId: UUID? = null,
    @Column("created_at") val createdAt: OffsetDateTime,
    @Column("deleted_at") val deletedAt: OffsetDateTime? = null,
)