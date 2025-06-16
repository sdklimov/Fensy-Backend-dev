package ru.fensy.dev.graphql.controller.user

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.usecase.user.GetUserUseCase

@Controller
class GetUserGraphQLController(
    private val getUserUseCase: GetUserUseCase,
) {

    @QueryMapping("getUser")
    suspend fun getUser(env: DataFetchingEnvironment): UserResponse = getUserUseCase.execute(env)

}
