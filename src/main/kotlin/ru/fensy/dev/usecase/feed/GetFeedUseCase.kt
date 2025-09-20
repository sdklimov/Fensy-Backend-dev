package ru.fensy.dev.usecase.feed

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.graphql.controller.post.response.PostsResponse
import ru.fensy.dev.extensions.pageNumber
import ru.fensy.dev.extensions.pageSize
import ru.fensy.dev.properties.PostProperties
import ru.fensy.dev.repository.FeedRepository
import ru.fensy.dev.usecase.BaseUseCase

/**
 * Получить ленту
 */
@Component
@Transactional(readOnly = true)
class GetFeedUseCase(
    private val postProperties: PostProperties,
    private val feedRepository: FeedRepository,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): PostsResponse {
        val pageNumber = env.pageNumber()
        val pageSize = env.pageSize(postProperties.pagination.pageSize)

        val pageRq = PageRequest(pageNumber, pageSize)

        val user = currentUser(false)

        val posts = user?.let {
            feedRepository.getForUser(it.id!!, pageRq)
        } ?: feedRepository.getForAll(pageRq)

        return PostsResponse(posts = posts, message = "Лента получена", success = true)
    }

}
