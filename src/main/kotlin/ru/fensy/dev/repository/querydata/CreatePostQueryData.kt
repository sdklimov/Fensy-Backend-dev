package ru.fensy.dev.repository.querydata

import ru.fensy.dev.domain.PostAllowVieweingFor

data class CreatePostQueryData(
    val authorId: Long,
    val title: String?,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean,
    val originalPostId: Long? = null,
)
