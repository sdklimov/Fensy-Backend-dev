package ru.fensy.dev.graphql.controller.post

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.usecase.post.GetAllUserPostsUseCase

@Controller
class GetAllUserPostsGraphQLController(
    private val getAllUserPostsUseCase: GetAllUserPostsUseCase,
) {

    @QueryMapping("getAllUserPosts")
    suspend fun getAllUserPosts(env: DataFetchingEnvironment): PostsResponse = getAllUserPostsUseCase.execute(env)

}
