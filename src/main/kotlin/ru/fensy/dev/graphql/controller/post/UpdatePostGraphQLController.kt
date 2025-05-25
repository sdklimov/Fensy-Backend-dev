package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.UpdatePostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.UpdatePostUseCase

@Controller
class UpdatePostGraphQLController(
    private val updatePostUseCase: UpdatePostUseCase,
) {

    @MutationMapping("updatePost")
    suspend fun updatePost(
        @Argument input: UpdatePostInput,
    ): PostResponse {
        return updatePostUseCase.execute(input)
    }

}
