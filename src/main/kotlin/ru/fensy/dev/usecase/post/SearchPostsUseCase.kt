package ru.fensy.dev.usecase.post

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.extensions.Extensions.toLikeQuery
import ru.fensy.dev.graphql.controller.post.response.SearchPostsResponse
import ru.fensy.dev.repository.PostRepository

/**
 * Получить все посты пользователя
 */
@Component
@Transactional(readOnly = true)
class SearchPostsUseCase(
    private val postRepository: PostRepository,
) {

    suspend fun execute(env: DataFetchingEnvironment): SearchPostsResponse = coroutineScope {

        val arguments = ((env.arguments as Map<String, Any>).entries.first().value as Map<String, Any>).toMap()
        val query = arguments["query"] as String
        val limit = arguments["limit"] as Int
        val offset = arguments["offset"] as Int

        val likeQuery = query.toLikeQuery()
        val postsDeferred = async { postRepository.search(query = likeQuery, limit = limit, offset = offset) }
        val totalDeferred = async { postRepository.count(query = likeQuery) }
        return@coroutineScope SearchPostsResponse(
            posts = postsDeferred.await(),
            total = totalDeferred.await(),
            limit = limit,
            offset = offset,
            message = "",
            success = false
        )
    }

}
