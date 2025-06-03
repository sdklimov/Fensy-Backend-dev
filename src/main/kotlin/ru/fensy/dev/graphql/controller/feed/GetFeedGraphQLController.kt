package ru.fensy.dev.graphql.controller.feed

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.usecase.feed.GetFeedUseCase

@Controller
class GetFeedGraphQLController(
    private val getFeedUseCase: GetFeedUseCase,
) {

    @QueryMapping("getFeed")
    suspend fun getFeed(
        env: DataFetchingEnvironment,
    ): PostsResponse {
        return getFeedUseCase.execute(env)
    }

}
