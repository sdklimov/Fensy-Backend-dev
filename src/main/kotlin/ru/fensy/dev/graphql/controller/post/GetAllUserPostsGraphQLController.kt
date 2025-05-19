package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.usecase.post.GetAllUserPostsUseCase

@Controller
class GetAllUserPostsGraphQLController(
    private val getAllUserPostsUseCase: GetAllUserPostsUseCase,
) {

    @QueryMapping("getAllUserPosts")
    suspend fun getAllUserPosts(@Argument username: String): PostsResponse = getAllUserPostsUseCase.execute(username)

}
