package ru.fensy.dev.domain

data class PostReaction(
    val id: Long? = null,
    val postId: Long,
    val userId: Long,
    val emoji: String,
)
