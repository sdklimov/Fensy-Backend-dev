package ru.fensy.dev.graphql.controller.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.auth.response.AuthResponse
import ru.fensy.dev.usecase.auth.DoAuthUseCase

/**
 * Резолвер авторизации
 */
@Controller
class AuthGraphQLController(
    private val authUseCase: DoAuthUseCase,
) {

    @MutationMapping("auth")
    suspend fun auth(env: DataFetchingEnvironment): AuthResponse {
        return authUseCase.execute(env)
    }

}
