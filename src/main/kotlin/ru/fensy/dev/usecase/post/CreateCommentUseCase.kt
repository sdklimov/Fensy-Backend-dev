package ru.fensy.dev.usecase.post

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.input.CreateCommentRequest
import ru.fensy.dev.graphql.controller.post.response.CommentResponse
import ru.fensy.dev.repository.CommentsRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class CreateCommentUseCase(
    private val commentsRepository: CommentsRepository,
) : BaseUseCase() {

    suspend fun execute(request: CreateCommentRequest): CommentResponse {
        val currentUserId = currentUser(true)!!.id!!

        val parentPostId = request.parentCommentId?.let {
            commentsRepository.getById(it)
        }

        parentPostId?.let {
            if (it.postId != request.postId) {
                throw IllegalArgumentException("В комментарии с id = [${request.postId} отсутствует родительский комментарий с id = [${request.parentCommentId}]]")
            }
        }

        val comment = commentsRepository.create(
            content = request.content,
            authorId = currentUserId,
            postId = request.postId,
            parentId = request.parentCommentId
        )

        return CommentResponse(message = "Комментарий успешно добавлен", success = true, comment = comment)

    }

}
