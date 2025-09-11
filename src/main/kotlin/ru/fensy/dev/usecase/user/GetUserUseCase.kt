package ru.fensy.dev.usecase.user

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.usecase.BaseUseCase

/**
 * Получить пользователя
 */
@Component
class GetUserUseCase(
    private val userRepository: UserRepository,
): BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): UserResponse {
        val idArg = env.getArgument<Any?>("id")

		val username = env.getArgument<String>("username")

		// try id first
		idArg?.let {
			val id = when (it) {
				is Int -> it.toLong()
				is Long -> it
				is String -> it.toLongOrNull()
				else -> null
			} ?: throw IllegalArgumentException("Invalid id argument")

			val user = userRepository.findById(id)
			return UserResponse(user = user, message = "Пользователь получен успешно", success = true)
		}

        return username?.let {
            val user = userRepository.findByUsername(it)
            UserResponse(user = user, message = "Пользователь получен успешно", success = true)

        } ?: UserResponse(user = currentUser(required = true), success = true, message = "Пользователь получен успешно")

    }

}
