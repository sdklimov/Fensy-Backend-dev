package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.GetPostByIdUseCase

@Controller
class GetPostGraphQLController(
    private val getPostByIdUseCase: GetPostByIdUseCase,

    ) {

    @QueryMapping("getPost")
    suspend fun getPost(@Argument postId: Long): PostResponse {
        val post =  getPostByIdUseCase.execute(postId)
        return PostResponse(post)
    }

}