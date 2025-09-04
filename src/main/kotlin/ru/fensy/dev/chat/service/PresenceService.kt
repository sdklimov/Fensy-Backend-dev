package ru.fensy.dev.chat.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import ru.fensy.dev.chat.dto.PresenceDto
import ru.fensy.dev.chat.dto.toDto
import ru.fensy.dev.chat.model.PresenceEntity
import ru.fensy.dev.chat.repo.PresenceRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.ConcurrentHashMap

@Service
class PresenceService(private val repo: PresenceRepository) {
    private val sink = Sinks.many().multicast().onBackpressureBuffer<PresenceDto>()
    private val mem = ConcurrentHashMap<String, PresenceDto>()

    fun get(userIds: List<String>): Flux<PresenceDto> =
        repo.findByUserIdIn(userIds)
            .map { it.toDto() }

    fun connect(userId: String): Mono<PresenceDto> = set(userId, online = true)
    fun disconnect(userId: String): Mono<PresenceDto> = set(userId, online = false)
    fun heartbeat(userId: String): Mono<PresenceDto> = set(userId, online = true)

    private fun set(userId: String, online: Boolean): Mono<PresenceDto> {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val entity = PresenceEntity(userId = userId, online = online, lastSeen = now)
        return repo.save(entity)
            .map { it.toDto() }
            .doOnNext { dto ->
                mem[userId] = dto
                sink.tryEmitNext(dto)
            }
    }

    fun events(): Flux<PresenceDto> = sink.asFlux()
}