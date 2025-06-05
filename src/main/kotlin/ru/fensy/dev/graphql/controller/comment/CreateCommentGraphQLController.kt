package ru.fensy.dev.graphql.controller.comment

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.CreateCommentRequest
import ru.fensy.dev.graphql.controller.post.response.CommentResponse
import ru.fensy.dev.usecase.post.CreateCommentUseCase

@Controller
class CreateCommentGraphQLController(
    private val createCommentUseCase: CreateCommentUseCase,
) {

    @MutationMapping("createComment")
    suspend fun createComment(
        @Argument input: CreateCommentRequest,
    ): CommentResponse = createCommentUseCase.execute(input)

}
