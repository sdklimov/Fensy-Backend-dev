package ru.fensy.dev.graphql.controller.post.input

import ru.fensy.dev.domain.AllowViewingFor

data class UpdatePostInput(
    val id: Long,
    val title: String,
    val content: String,
    val allowViewingFor: AllowViewingFor,
    val pinned: Boolean = false,
    val tags: List<String> = emptyList(),
//    val attachments: List<FilePart>? = emptyList(),
    val parsedLinks: List<CreatedParsedLink> = emptyList(),
    val interestIds: List<Long>,
    val collectionIds: List<Long>,
    )
