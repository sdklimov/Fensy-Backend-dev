package ru.fensy.dev.graphql.controller.post.input

import ru.fensy.dev.domain.ParsedLinkType

data class CreatedParsedLink(
    val type: ParsedLinkType,
    val link: String,
    val picture: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val interestIds: List<Long>,
)