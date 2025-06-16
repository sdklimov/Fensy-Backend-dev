package ru.fensy.dev.usecase.user

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.usecase.BaseUseCase
import ru.fensy.dev.usecase.post.operationmodel.CreatePostOperationRq

/**
 * Получить пользователя
 */
@Component
class GetUserUsaCase(
    private val userRepository: UserRepository,
): BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): UserResponse {
        val username = env.getArgument<String>("username")

        return username?.let {
            val user = userRepository.findByUsername(it)
            UserResponse(user = user, message = "Пользователь получен успешно", success = true)

        } ?: UserResponse(user = currentUser(required = true), success = true, message = "Пользователь получен успешно")

    }

}
