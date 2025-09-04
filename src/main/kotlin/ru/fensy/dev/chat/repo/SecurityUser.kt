package ru.fensy.dev.chat.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono

object CurrentUser {
    fun id(): Mono<String> = ReactiveSecurityContextHolder.getContext()
        .mapNotNull { it.authentication }
        .flatMap { auth -> Mono.justOrEmpty(extract(auth)) }
        .switchIfEmpty(Mono.error(IllegalStateException("Unauthenticated")))

    private fun extract(auth: Authentication): String? {
        val principal = auth.principal
        return when (principal) {
            is Jwt -> principal.claims["user_id"]?.toString()
                ?: principal.subject
            else -> auth.name // если ваш UserDetails#setUsername = userId
        }
    }
}