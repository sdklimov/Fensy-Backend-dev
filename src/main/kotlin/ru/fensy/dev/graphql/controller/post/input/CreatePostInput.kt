package ru.fensy.dev.graphql.controller.post.input

data class CreatePostInput(
    val originalPostId: Long?,
    val isRepost: Boolean?,
    val authorId: Long,
    val title: String?,
    val content: String?,
    val allowViewingFor: String,
    val pinned: Boolean?,
    val adultContent: Boolean?
)
