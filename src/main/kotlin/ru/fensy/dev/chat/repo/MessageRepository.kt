package ru.fensy.dev.chat.repo

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.fensy.dev.chat.model.MessageEntity
import java.time.OffsetDateTime
import java.util.UUID

interface MessageRepository : ReactiveCrudRepository<MessageEntity, UUID> {

    @Query("""
        SELECT * FROM messages
        WHERE (
          (sender_id = :user AND recipient_id = :peer) OR
          (sender_id = :peer AND recipient_id = :user)
        ) AND (:before IS NULL OR created_at < :before)
		  AND (:after IS NULL OR created_at > :after)
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    fun dialog(user: String, peer: String, limit: Int, before: OffsetDateTime?, after: OffsetDateTime?): Flux<MessageEntity>

    @Query("""
        SELECT DISTINCT ON (peer) * FROM (
          SELECT CASE WHEN sender_id = :user THEN recipient_id ELSE sender_id END AS peer,
                 id, sender_id, recipient_id, content, reply_to_id, created_at, deleted_at
          FROM messages
          WHERE sender_id = :user OR recipient_id = :user
          ORDER BY peer, created_at DESC
        ) t
        ORDER BY peer
        LIMIT :limit OFFSET :offset
    """)
    fun chatSummaries(user: String, limit: Int, offset: Int): Flux<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :id AND sender_id = :user")
    fun findOwned(id: UUID, user: String): Mono<MessageEntity>
}