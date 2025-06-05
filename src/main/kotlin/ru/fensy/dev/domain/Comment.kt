package ru.fensy.dev.domain

data class Comment(
    val id: Long,
    val content: String,
    val authorId: Long,
    val postId: Long,
    val parentId: Long? = null,
    val hasChildren: Boolean
    )
