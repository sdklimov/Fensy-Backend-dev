package ru.fensy.dev.graphql.controller

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.domain.Post
import ru.fensy.dev.graphql.input.CreatePostInput
import ru.fensy.dev.usecase.CreatePostUseCase

@Controller
class PostGraphQLController(
    private val createPostUseCase: CreatePostUseCase,

    ) {

    @MutationMapping("createPost")
    suspend fun createPost(@Argument input: CreatePostInput): Post {
        val createdPostId = createPostUseCase.execute(input)
        return createdPostId
    }

}
