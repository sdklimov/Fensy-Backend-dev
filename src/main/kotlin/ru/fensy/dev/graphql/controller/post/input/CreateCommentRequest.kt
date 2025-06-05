package ru.fensy.dev.graphql.controller.post.input

data class CreateCommentRequest(
    val postId: Long,
    val content: String,
    val parentCommentId: Long? = null
)