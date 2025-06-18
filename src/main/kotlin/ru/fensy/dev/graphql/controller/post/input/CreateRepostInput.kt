package ru.fensy.dev.graphql.controller.post.input

import java.util.UUID
import ru.fensy.dev.domain.AllowViewingFor

data class CreateRepostInput(
    val originalPostId: Long,
    val title: String? = null,
    val content: String,
    val allowViewingFor: AllowViewingFor,
    val pinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val attachments: List<UUID>? = emptyList(),
    val fileSessionId: String? = null,
    val parsedLinks: List<CreatedParsedLink>? = emptyList(),
    val interestIds: List<Long> = emptyList(),
    val collectionIds: List<Long> = emptyList(),
    )
