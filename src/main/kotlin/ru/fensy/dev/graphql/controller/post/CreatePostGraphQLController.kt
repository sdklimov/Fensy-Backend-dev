package ru.fensy.dev.graphql.controller.post

import java.util.UUID
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.CreatePostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.CreatePostUseCase
import ru.fensy.dev.usecase.post.operationmodel.CreatePostOperationRq

@Controller
class CreatePostGraphQLController(
    private val createPostUseCase: CreatePostUseCase,
) {

    @MutationMapping("createPost")
    suspend fun createPost(
        @Argument input: CreatePostInput,
    ): PostResponse =
        createPostUseCase.execute(
            CreatePostOperationRq(
                title = input.title,
                content = input.content,
                allowViewingFor = input.allowViewingFor,
                pinned = input.pinned,
                tags = input.tags,
                parsedLinks = input.parsedLinks,
                interestIds = input.interestIds,
                collectionIds = input.collectionIds,
                fileSessionId = input.fileSessionId?.let { UUID.fromString(it) },
                attachments = input.attachments?.map { UUID.fromString(it) },
            )
        )

}
