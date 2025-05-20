package ru.fensy.dev.usecase.user

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.repository.UserRepository

/**
 * Получить пользователя
 */
@Component
class GetUserUsaCase(
    private val userRepository: UserRepository,
) {

    suspend fun execute(env: DataFetchingEnvironment): UserResponse {
        val username = env.getArgument<String>("username")

        return username?.let {
            val user = userRepository.findByUsername(it)
            UserResponse(user = user, message = "Пользователь получен успешно", success = true)

        } ?: UserResponse(user = null, message = "Пользователь не найден", success = false)

    }

}
