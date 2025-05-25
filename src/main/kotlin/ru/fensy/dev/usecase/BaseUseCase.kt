package ru.fensy.dev.usecase

import jakarta.annotation.Nullable
import kotlinx.coroutines.reactor.awaitSingleOrNull
import reactor.core.publisher.Mono
import ru.fensy.dev.constants.CURRENT_USER_CONTEXT_KEY
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
            Mono.just(ctx.get<User>(CURRENT_USER_CONTEXT_KEY))
        }.awaitSingleOrNull().let { userNullable ->
            if (required && userNullable == null) {
                throw USER_NOT_EXISTS_IN_CONTEXT_EXCEPTION
            } else userNullable
        }
    }

    companion object {
        private val USER_NOT_EXISTS_IN_CONTEXT_EXCEPTION =
            UserNotExistsInContextException("Пользователь не найден в контексте. Доступ запрещен.")
    }

}
