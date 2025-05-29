package ru.fensy.dev.usecase

import jakarta.annotation.Nullable
import kotlin.jvm.optionals.getOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono
import ru.fensy.dev.constants.CURRENT_USER_CONTEXT_KEY
import ru.fensy.dev.constants.REQUEST_HTTP_HEADERS
import ru.fensy.dev.domain.User
import ru.fensy.dev.exception.UserNotExistsInContextException

open class BaseUseCase {

    /**
     * Получить текущего пользователя из контекста.
     * Может быть null
     */
    @Nullable
    suspend fun currentUser(required: Boolean): User? {
        return Mono.deferContextual { ctx ->
            ctx.getOrEmpty<User>(CURRENT_USER_CONTEXT_KEY).getOrNull()?.let { Mono.just(it) }
                ?: Mono.empty()
        }.awaitSingleOrNull().let { userNullable ->
            if (required && userNullable == null) {
                throw USER_NOT_EXISTS_IN_CONTEXT_EXCEPTION
            } else userNullable
        }
    }

    @Nullable
    suspend fun getHeaders(): HttpHeaders {
        return Mono.deferContextual { ctx ->
            Mono.just(ctx.get<HttpHeaders>(REQUEST_HTTP_HEADERS))
        }.awaitSingle()
    }

    companion object {
        private val USER_NOT_EXISTS_IN_CONTEXT_EXCEPTION =
            UserNotExistsInContextException("Пользователь не найден в контексте. Доступ запрещен.")
    }

}
