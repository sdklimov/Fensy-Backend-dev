package ru.fensy.dev.usecase.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import ru.fensy.dev.graphql.controller.auth.response.RefreshResponse

@Component
class RefreshTokenUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): RefreshResponse {

        return RefreshResponse(accessToken = null, message = "porro", success = false)
    }

}
