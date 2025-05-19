package ru.fensy.dev.repository.querydata

import ru.fensy.dev.domain.ParsedLinkType

data class CreateParsedLinkQueryData(
    val postId: Long,
    val type: ParsedLinkType,
    val link: String,
    val picture: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
)
