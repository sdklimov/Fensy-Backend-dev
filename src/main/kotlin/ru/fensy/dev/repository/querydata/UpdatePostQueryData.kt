package ru.fensy.dev.repository.querydata

import ru.fensy.dev.domain.AllowViewingFor

data class UpdatePostQueryData(
    val id: Long,
    val authorId: Long,
    val title: String?,
    val content: String,
    val allowViewingFor: AllowViewingFor,
    val pinned: Boolean,
)
