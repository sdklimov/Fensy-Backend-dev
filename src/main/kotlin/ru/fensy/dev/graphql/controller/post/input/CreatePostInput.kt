package ru.fensy.dev.graphql.controller.post.input

import org.springframework.http.codec.multipart.FilePart
import ru.fensy.dev.domain.PostAllowVieweingFor

data class CreatePostInput(
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
