package ru.fensy.dev.graphql.controller.post

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.graphql.controller.post.response.SearchPostsResponse
import ru.fensy.dev.usecase.post.GetAllUserPostsUseCase
import ru.fensy.dev.usecase.post.SearchPostsUseCase

@Controller
class SearchPostsGraphQLController(
    private val searchPostsUseCase: SearchPostsUseCase,
) {

    @QueryMapping("searchPosts")
    suspend fun searchPosts(env: DataFetchingEnvironment): SearchPostsResponse = searchPostsUseCase.execute(env)

}
