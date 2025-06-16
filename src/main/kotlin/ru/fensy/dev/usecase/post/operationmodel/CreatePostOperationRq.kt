package ru.fensy.dev.usecase.post.operationmodel

import java.util.UUID
import ru.fensy.dev.domain.PostAllowVieweingFor
import ru.fensy.dev.graphql.controller.post.input.CreatedParsedLink

data class CreatePostOperationRq(
    val originalPostId: Long? = null,
    val title: String,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val attachments: List<UUID>? = emptyList(),
    val fileSessionId: UUID?,
    val parsedLinks: List<CreatedParsedLink>? = emptyList(),
    val interestIds: List<Long>,
    val collectionIds: List<Long> = emptyList(),
)
