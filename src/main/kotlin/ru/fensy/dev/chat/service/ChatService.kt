package ru.fensy.dev.chat.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import ru.fensy.dev.chat.dto.ChatSummaryDto
import ru.fensy.dev.chat.dto.MessageDto
import ru.fensy.dev.chat.dto.toDto
import ru.fensy.dev.chat.model.MessageEntity
import ru.fensy.dev.chat.repo.MessageRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatService(
    private val repo: MessageRepository,
    private val template: R2dbcEntityTemplate
) {

    private val sinks = ConcurrentHashMap<String, Sinks.Many<MessageEvent>>()
    private fun sink(userId: String) =
        sinks.computeIfAbsent(userId) { Sinks.many().multicast().onBackpressureBuffer() }

    fun send(from: String, to: String, content: String, replyTo: UUID?): Mono<MessageDto> {
        val e = MessageEntity(
            id = UUID.randomUUID(),
            senderId = from,
            recipientId = to,
            content = content,
            replyToId = replyTo,
            createdAt = OffsetDateTime.now(ZoneOffset.UTC),
        )

        // Явно указываем generic, чтобы Kotlin корректно понял тип возвращаемого Mono
        return template.insert<MessageEntity>(e)
            .map { it.toDto() }
            .doOnNext { m -> emit(MessageEvent.Created(m), from, to) }
    }

    fun dialog(user: String, peer: String, limit: Int, before: OffsetDateTime?, after: OffsetDateTime?): Flux<MessageDto> =
        repo.dialog(user, peer, limit, before, after)
            .map { it.toDto() }
            .sort(compareBy { it.createdAt })

    fun delete(user: String, messageId: UUID): Mono<Boolean> =
        repo.findOwned(messageId, user)
            .flatMap { owned ->
                // обновляем content => пометка удаления
                repo.save(owned.copy(content = "", deletedAt = OffsetDateTime.now(ZoneOffset.UTC)))
            }
            .doOnNext { m -> emit(MessageEvent.Deleted(requireNotNull(m.id)), m.senderId, m.recipientId) }
            .map { true }

    fun summaries(user: String, limit: Int, offset: Int): Flux<ChatSummaryDto> =
    repo.chatSummaries(user, limit, offset)
        .groupBy { if (it.senderId == user) it.recipientId else it.senderId }
        .flatMap { grp ->
            grp.next().map { top ->
                ChatSummaryDto(
                    peerId = if (top.senderId == user) top.recipientId else top.senderId,
                    lastMessage = top.toDto(),
                    unreadCount = 0
                )
            }
        }


    fun events(user: String): Flux<MessageEvent> = sink(user).asFlux()

    private fun emit(event: MessageEvent, a: String, b: String) {
        sink(a).tryEmitNext(event)
        sink(b).tryEmitNext(event)
    }
}

sealed interface MessageEvent {
    data class Created(val message: MessageDto) : MessageEvent
    data class Deleted(val messageId: UUID) : MessageEvent
}
