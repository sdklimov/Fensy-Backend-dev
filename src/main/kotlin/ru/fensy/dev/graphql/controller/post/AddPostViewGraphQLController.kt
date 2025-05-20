package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.AddPostViewUseCase

@Controller
class AddPostViewGraphQLController(
    private val addPostViewUseCase: AddPostViewUseCase,
) {

    @MutationMapping("addPostView")
    suspend fun addPostView(
        @Argument postId: Long,
    ): PostResponse {
        return addPostViewUseCase.execute(postId)
    }

}
