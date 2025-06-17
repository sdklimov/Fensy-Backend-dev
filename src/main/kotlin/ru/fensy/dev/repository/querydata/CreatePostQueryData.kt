package ru.fensy.dev.repository.querydata

import ru.fensy.dev.domain.AllowViewingFor

data class CreatePostQueryData(
    val authorId: Long,
    val title: String?,
    val content: String,
    val allowViewingFor: AllowViewingFor,
    val pinned: Boolean,
    val originalPostId: Long? = null,
)
