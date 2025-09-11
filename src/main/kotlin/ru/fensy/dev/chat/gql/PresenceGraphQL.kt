package ru.fensy.dev.chat.gql

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
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
    suspend fun presenceHeartbeat(): PresenceDto {
        val uid = CurrentUser.idSuspend()
        return presence.heartbeat(uid).awaitSingle()
    }
}
