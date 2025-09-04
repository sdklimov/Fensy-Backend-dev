package ru.fensy.dev.chat.gql

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.fensy.dev.chat.dto.ChatSummaryDto
import ru.fensy.dev.chat.dto.MessageDto
import ru.fensy.dev.chat.security.CurrentUser
import ru.fensy.dev.chat.service.ChatService
import java.time.OffsetDateTime
import java.util.UUID

@Controller
class ChatGraphQL(private val chat: ChatService) {

    @QueryMapping
    fun messages(@Argument peerId: String, @Argument limit: Int?, @Argument before: OffsetDateTime?): Flux<MessageDto> =
        CurrentUser.id().flatMapMany { me ->
            val lim = (limit ?: 50).coerceIn(1, 200)
            chat.dialog(me, peerId, lim, before)
        }

    @QueryMapping
    fun chats(@Argument limit: Int?, @Argument offset: Int?): Flux<ChatSummaryDto> =
        CurrentUser.id().flatMapMany { me -> chat.summaries(me, (limit ?: 50).coerceIn(1, 200), (offset ?: 0).coerceAtLeast(0)) }

    @MutationMapping
    fun sendMessage(@Argument toUserId: String, @Argument content: String, @Argument replyToId: UUID?): Mono<MessageDto> =
        CurrentUser.id().flatMap { me -> chat.send(me, toUserId, content, replyToId) }

    @MutationMapping
    fun deleteMessage(@Argument messageId: UUID): Mono<Boolean> =
        CurrentUser.id().flatMap { me -> chat.delete(me, messageId) }

    @SubscriptionMapping
    fun messageEvents(@Argument peerId: String?): Flux<Any> =
        CurrentUser.id().flatMapMany { me ->
            chat.events(me).filter { ev ->
                when (ev) {
                    is ru.fensy.dev.chat.service.MessageEvent.Created ->
                        peerId == null || ev.message.senderId == peerId || ev.message.recipientId == peerId
                    is ru.fensy.dev.chat.service.MessageEvent.Deleted -> true
                }
            }.map { ev -> when (ev) {
                is ru.fensy.dev.chat.service.MessageEvent.Created -> MessageCreated(ev.message)
                is ru.fensy.dev.chat.service.MessageEvent.Deleted -> MessageDeleted(ev.messageId)
            } }
        }
}

class MessageCreated(val message: MessageDto)
class MessageDeleted(val messageId: UUID)