package ru.fensy.dev.graphql.controller.post.input

import java.util.UUID
import ru.fensy.dev.domain.PostAllowVieweingFor

data class CreateRepostInput(
    val originalPostId: Long,
    val title: String,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val attachments: List<UUID>? = emptyList(),
    val fileSessionId: String? = null,
    val parsedLinks: List<CreatedParsedLink>? = emptyList(),
    val interestIds: List<Long>,
    val collectionIds: List<Long>,
    )
