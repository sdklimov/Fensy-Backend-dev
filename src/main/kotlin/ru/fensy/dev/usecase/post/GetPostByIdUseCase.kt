package ru.fensy.dev.usecase.post

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Post
import ru.fensy.dev.repository.PostRepository

/**
 * Получить пост
 */
@Component
@Transactional(readOnly = true)
class GetPostByIdUseCase(
    private val postRepository: PostRepository,
) {

    suspend fun execute(id: Long): Post {
        return postRepository.findById(id)
    }

}