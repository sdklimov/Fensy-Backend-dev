package ru.fensy.dev.usecase.post

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.repository.PostViewsRepository
import ru.fensy.dev.usecase.BaseUseCase

/**
 * Получить пост
 */
@Component
@Transactional
class AddPostViewUseCase(
    private val postViewsRepository: PostViewsRepository,
    private val interestsRepository: InterestsRepository,
): BaseUseCase() {

    suspend fun execute(id: Long): PostResponse {
        val userId: Long? = currentUser(false)?.id
        val ipAddress = getIp()

        postViewsRepository.addPostView(id, ipAddress)

        userId?.let { currentUserId ->
            val postInterests = interestsRepository.findByPostId(id)

            postInterests.takeIf { it.isNotEmpty() }?.let {
                interestsRepository.addUserInterests(currentUserId, postInterests.map { i -> i.id })
            }
        }

        return PostResponse(post = null, message = "Пост просмотрен", success = true)

    }

}
