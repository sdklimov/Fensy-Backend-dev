package ru.fensy.dev.graphql.controller.post

import java.util.UUID
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.CreateRepostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.CreatePostUseCase
import ru.fensy.dev.usecase.post.operationmodel.CreatePostOperationRq

@Controller
class CreateRepostGraphQLController(
    private val createPostUseCase: CreatePostUseCase,
) {

    @MutationMapping("createRepost")
    suspend fun createRepost(
        @Argument input: CreateRepostInput,
    ): PostResponse =
        createPostUseCase.execute(
            CreatePostOperationRq(
                originalPostId = input.originalPostId,
                title = input.title,
                content = input.content,
                allowViewingFor = input.allowViewingFor,
                pinned = input.pinned,
                tags = input.tags,
                parsedLinks = input.parsedLinks,
                interestIds = input.interestIds,
                collectionIds = input.collectionIds,
                fileSessionId = input.fileSessionId?.let { UUID.fromString(it) },
            )
        )

}
