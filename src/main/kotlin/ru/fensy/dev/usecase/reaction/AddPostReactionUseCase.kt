package ru.fensy.dev.usecase.reaction

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.PostReaction
import ru.fensy.dev.exception.PostNotFoundException
import ru.fensy.dev.repository.PostReactionRepository
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.rest.reactions.ReactionRq
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class AddPostReactionUseCase(
    private val postReactionRepository: PostReactionRepository,
    private val postRepository: PostRepository,
) : BaseUseCase() {

    suspend fun execute(postId: Long, reaction: ReactionRq) {
        val currentUserId = currentUser(true)!!.id!!

        postRepository.findById(postId) ?: throw PostNotFoundException()

        postReactionRepository.checkReactionExists(postId, reaction.reaction, userId = currentUserId)
            ?.let {
                postReactionRepository.deleteById(it.id!!)
            } ?: postReactionRepository.create(
            PostReaction(
                postId = postId, userId = currentUserId, emoji = reaction.reaction
            )
        )
    }

}
