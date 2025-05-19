package ru.fensy.dev.usecase.post

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.repository.PostRepository

/**
 * Получить пост
 */
@Component
@Transactional(readOnly = true)
class GetPostByIdUseCase(
    private val postRepository: PostRepository,
) {

    private val logger = KotlinLogging.logger { }

    suspend fun execute(id: Long): PostResponse =
        postRepository
            .runCatching { findById(id) }
            .fold(
                onSuccess = { PostResponse(message = "Пост успешно получен", post = it) },
                onFailure = {
                    logger.error(it) { "Ошибка получения поста с id = $id" }
                    PostResponse(message = "Ошибка получения поста", success = false)
                }
            )

}
