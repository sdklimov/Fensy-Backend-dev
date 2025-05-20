package ru.fensy.dev.usecase.post.operationmodel

import ru.fensy.dev.domain.PostAllowVieweingFor
import ru.fensy.dev.graphql.controller.post.input.CreatedParsedLink

data class CreatePostOperationRq(
    val originalPostId: Long? = null,
    val title: String,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean = false,
    val tags: List<String> = emptyList(),
//    val attachments: List<FilePart>? = emptyList(),
    val parsedLinks: List<CreatedParsedLink>? = emptyList(),
    val interestIds: List<Long>,
    val collectionIds: List<Long>,
)
