package ru.fensy.dev.usecase.post

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.repository.PostRepository

/**
 * Создать пост
 */
@Component
@Transactional
class CreatePostUseCase(
    private val postRepository: PostRepository,
) {

//    suspend fun execute(input: CreatePostInput): Post {
//        val authorId = 5L
//        return postRepository.create(
//            Post(
//                originalPostId = input.originalPostId,
//                isRepost = input.isRepost,
//                authorId = authorId,
//                title = input.title,
//                content = input.content,
//                allowViewingFor = input.allowViewingFor,
//                pinned = false,
//                adultContent = false
//            )
//        )
//    }

}