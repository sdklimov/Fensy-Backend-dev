package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.CreatePostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.CreatePostUseCase

@Controller
class CreatePostGraphQLController(
    private val createPostUseCase: CreatePostUseCase,
    ) {

    @MutationMapping("createPost")
    suspend fun createPost(
        @Argument input: CreatePostInput,
    ): PostResponse =
        createPostUseCase.execute(input)

}
