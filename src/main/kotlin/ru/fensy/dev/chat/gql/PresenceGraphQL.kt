package ru.fensy.dev.chat.gql

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.fensy.dev.chat.dto.PresenceDto
import ru.fensy.dev.chat.security.CurrentUser
import ru.fensy.dev.chat.service.PresenceService

@Controller
class PresenceGraphQL(private val presence: PresenceService) {

    @QueryMapping
    fun presence(@Argument userIds: List<String>): Flux<PresenceDto> = presence.get(userIds)

    @SubscriptionMapping
    fun presenceEvents(): Flux<PresenceDto> = presence.events()

    // хук: сообщаем о connect/disconnect/heartbeat
    @MutationMapping
    fun presenceHeartbeat(): Mono<PresenceDto> = CurrentUser.id().flatMap { uid -> presence.heartbeat(uid) }
}