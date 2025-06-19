package ru.fensy.dev.rest.reactions

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.usecase.reaction.AddPostReactionUseCase

@RestController
@RequestMapping(path = ["/api/v1/posts/{postId}/reactions"])
class AddReactionToPostController(
    private val addPostReactionUseCase: AddPostReactionUseCase,
) {

    @PostMapping
    suspend fun downloadFile(
        @PathVariable("postId") postId: Long,
        @RequestBody reaction: ReactionRq,
    ) {
        addPostReactionUseCase.execute(postId, reaction)
    }

}

data class ReactionRq(
    val reaction: String,
)