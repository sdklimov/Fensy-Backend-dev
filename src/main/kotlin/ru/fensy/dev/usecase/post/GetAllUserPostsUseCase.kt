package ru.fensy.dev.usecase.post

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.repository.PostRepository

/**
 * Получить все посты пользователя
 */
@Component
@Transactional(readOnly = true)
class GetAllUserPostsUseCase(
    private val postRepository: PostRepository,
) {

    private val logger = KotlinLogging.logger { }

    suspend fun execute(userName: String): PostsResponse =
        postRepository
            .runCatching { findByUserName(userName) }
            .fold(
                onSuccess = { PostsResponse(message = "Посты успешно получены", posts = it) },
                onFailure = {
                    logger.error(it) { "Ошибка получения постов пользователя с username [$userName]" }
                    PostsResponse(message = "Ошибка получения постов", success = false)
                }
            )

}
