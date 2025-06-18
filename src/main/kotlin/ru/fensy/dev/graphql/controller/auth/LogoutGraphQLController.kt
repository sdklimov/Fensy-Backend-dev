package ru.fensy.dev.graphql.controller.auth

import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.BaseResponse
import ru.fensy.dev.usecase.auth.LogoutUseCase

@Controller
class LogoutGraphQLController(
    private val logoutUseCase: LogoutUseCase,
) {

    @MutationMapping("logout")
    suspend fun getAllInterests(): BaseResponse {
        return logoutUseCase.execute()
    }

}
