package ru.fensy.dev.graphql.controller.post.input

import ru.fensy.dev.domain.AllowViewingFor

data class CreateCollectionRq(
    val title: String,
    val description: String,
    val allowViewingFor: AllowViewingFor,
    val interestIds: List<Long>,
)
