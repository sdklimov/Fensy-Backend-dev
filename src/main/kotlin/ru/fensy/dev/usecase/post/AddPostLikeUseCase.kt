package ru.fensy.dev.usecase.post

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.LikeResponse
import ru.fensy.dev.repository.PostLikeRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class AddPostLikeUseCase(
    private val postLikeRepository: PostLikeRepository,
) : BaseUseCase() {

    suspend fun execute(postId: Long): LikeResponse {
        val currentUserId = currentUser(true)!!.id!!
        if (postLikeRepository.checkLikeExists(postId, currentUserId)) {
            postLikeRepository.delete(postId, currentUserId)
            return LikeResponse(success = true, message = "Post has been unliked")
        } else {
            postLikeRepository.addOrDeletePostLike(postId, currentUserId)
            return LikeResponse(success = true, message = "Post has been liked")
        }

    }

}
