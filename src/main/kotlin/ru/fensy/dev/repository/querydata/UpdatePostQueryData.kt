package ru.fensy.dev.repository.querydata

import ru.fensy.dev.domain.PostAllowVieweingFor

data class UpdatePostQueryData(
    val id: Long,
    val authorId: Long,
    val title: String?,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean,
)
