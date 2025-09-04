package ru.fensy.dev.chat.repo

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.fensy.dev.chat.model.PresenceEntity

interface PresenceRepository : ReactiveCrudRepository<PresenceEntity, String> {
    fun findByUserIdIn(userIds: Collection<String>): Flux<PresenceEntity>
    fun findByUserId(userId: String): Mono<PresenceEntity>
}