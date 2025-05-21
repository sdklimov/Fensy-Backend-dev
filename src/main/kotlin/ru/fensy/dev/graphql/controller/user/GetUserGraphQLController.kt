package ru.fensy.dev.graphql.controller.user

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.usecase.user.GetUserUsaCase

@Controller
class GetUserGraphQLController(
    private val getUserUsaCase: GetUserUsaCase,
) {

    @QueryMapping("getUser")
    suspend fun getUser(env: DataFetchingEnvironment): UserResponse = getUserUsaCase.execute(env)

}
