package ru.fensy.dev.graphql.controller.post

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.BaseResponse
import ru.fensy.dev.graphql.controller.post.response.LikeResponse
import ru.fensy.dev.usecase.post.AddPostLikeUseCase

@Controller
class AddLikeToPostGraphQLController(
    private val addPostLikeUseCase: AddPostLikeUseCase,
) {

    @MutationMapping("addLike")
    suspend fun likePost(
        @Argument postId: Long,
    ): LikeResponse = addPostLikeUseCase.execute(postId)

}
