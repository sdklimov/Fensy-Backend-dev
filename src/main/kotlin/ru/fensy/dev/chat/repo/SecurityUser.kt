package ru.fensy.dev.chat.security

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.deferContextual
import ru.fensy.dev.constants.Constants.CURRENT_USER_CONTEXT_KEY
import ru.fensy.dev.domain.User

/**
 * Suspend-версия: сначала пробуем SecurityContext, если нет — делаем fallback на Reactor Context (CURRENT_USER_CONTEXT_KEY).
 */
suspend fun idSuspend(): String {
    // 1) Попытка получить через ReactiveSecurityContextHolder (если настроен Resource Server)
    val fromSecurity = ReactiveSecurityContextHolder.getContext()
        .awaitSingleOrNull()
        ?.authentication
        ?.let { extractUserIdNullable(it) }

    if (!fromSecurity.isNullOrBlank()) return fromSecurity

    // 2) Fallback: попытка взять User из Reactor context (ValidateJwtWebFilter кладёт туда user)
    val fromCtx: String? = deferContextual { ctx ->
        if (ctx.hasKey(CURRENT_USER_CONTEXT_KEY)) {
            val u = ctx.get<User>(CURRENT_USER_CONTEXT_KEY)
            // u.id : Long? -> Mono<Long> via justOrEmpty -> map to Mono<String>
            Mono.justOrEmpty(u.id).map { it.toString() }
        } else {
            Mono.empty()
        }
    }.awaitSingleOrNull()

    return fromCtx ?: throw IllegalStateException("Unauthorized")
}

/**
 * Реактивная версия: сначала пытаемся SecurityContext, затем — Reactor Context, иначе — error.
 */
fun id(): Mono<String> =
    ReactiveSecurityContextHolder.getContext()
        .map { it.authentication }
        // извлекаем nullable строковый id из Authentication
        .map { extractUserIdNullable(it) }
        // фильтруем null-ы
        .filter { it != null }
        .map { it!! }
        .switchIfEmpty(
            deferContextual { ctx ->
                if (ctx.hasKey(CURRENT_USER_CONTEXT_KEY)) {
                    val u = ctx.get<User>(CURRENT_USER_CONTEXT_KEY)
                    // Mono.justOrEmpty(u.id) -> Mono<Long>, then .map -> Mono<String>
                    Mono.justOrEmpty(u.id).map { it.toString() }
                } else {
                    Mono.empty()
                }
            }
        )
        .switchIfEmpty(Mono.error(IllegalStateException("Unauthenticated")))

/**
 * Извлекает user id из Authentication или возвращает null.
 * Поддерживает Jwt (claim "user_id" или subject) и другие Authentication (auth.name).
 */
private fun extractUserIdNullable(auth: Authentication?): String? {
    if (auth == null) return null
    val principal = auth.principal
    return when (principal) {
        is Jwt -> principal.claims["user_id"]?.toString() ?: principal.subject
        else -> auth.name.takeIf { it.isNotBlank() } // если UserDetails#setUsername = userId
    }
}

/**
 * Backwards-compatible object: у проекта могут быть вызовы CurrentUser.id() / idSuspend().
 */
object CurrentUser {
    suspend fun idSuspend(): String = ru.fensy.dev.chat.security.idSuspend()
    fun id(): Mono<String> = ru.fensy.dev.chat.security.id()
}
