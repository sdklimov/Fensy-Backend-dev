package ru.fensy.dev.graphql.controller.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.auth.response.RefreshResponse
import ru.fensy.dev.usecase.auth.RefreshTokenUseCase

@Controller
class RefreshTokenController(
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {

    @MutationMapping("refreshAccessToken")
    suspend fun refreshToken(env: DataFetchingEnvironment): RefreshResponse {
        return refreshTokenUseCase.execute(env)
    }

}
