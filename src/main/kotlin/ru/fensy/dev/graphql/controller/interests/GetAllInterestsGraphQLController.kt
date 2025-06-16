package ru.fensy.dev.graphql.controller.interests

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.InterestsResponse
import ru.fensy.dev.usecase.interests.GetAllInterestsUseCase

@Controller
class GetAllInterestsGraphQLController(
    private val getAllInterestsUseCase: GetAllInterestsUseCase,
) {

    @QueryMapping("getAllInterests")
    suspend fun getAllInterests(
        env: DataFetchingEnvironment,
    ): InterestsResponse {
        return getAllInterestsUseCase.execute(env)
    }

}
