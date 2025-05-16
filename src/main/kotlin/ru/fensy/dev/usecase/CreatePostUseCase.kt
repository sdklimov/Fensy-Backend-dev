package ru.fensy.dev.usecase

import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Post
import ru.fensy.dev.graphql.input.CreatePostInput
import ru.fensy.dev.repository.PostRepository

/**
 * Создать пост
 */
@Component
class CreatePostUseCase(
    private val postRepository: PostRepository,
) {

    suspend fun execute(input: CreatePostInput): Post {
        val authorId = 5L
        return postRepository.create(
            Post(
                originalPostId = input.originalPostId,
                isRepost = input.isRepost,
                authorId = authorId,
                title = input.title,
                content = input.content,
                allowViewingFor = input.allowViewingFor,
                pinned = false,
                adultContent = false
            )
        )
    }

}
