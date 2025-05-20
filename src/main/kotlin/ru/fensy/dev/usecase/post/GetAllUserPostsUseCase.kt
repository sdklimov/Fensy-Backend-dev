package ru.fensy.dev.usecase.post

import graphql.schema.DataFetchingEnvironment
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.properties.PostProperties
import ru.fensy.dev.repository.PostRepository

/**
 * Получить все посты пользователя
 */
@Component
@Transactional(readOnly = true)
class GetAllUserPostsUseCase(
    private val postRepository: PostRepository,
    private val postProperties: PostProperties,
) {

    private val logger = KotlinLogging.logger { }

    suspend fun execute(env: DataFetchingEnvironment): PostsResponse {
        val userName = (((env.arguments as Map<String, Any>).entries.first().value) as String).toString()
        val pageNumber = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageNumber")?.getValue<Int>() ?: 1
        val pageSize = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageSize")?.getValue<Int>() ?: postProperties.pagination.pageSize

        val pageRq = PageRequest(pageNumber, pageSize)

        return postRepository
            .runCatching { findByUserName(userName, pageRq) }
            .fold(
                onSuccess = { PostsResponse(message = "Посты успешно получены", posts = it) },
                onFailure = {
                    logger.error(it) { "Ошибка получения постов пользователя с username [$userName]" }
                    PostsResponse(message = "Ошибка получения постов", success = false)
                }
            )
    }


}
