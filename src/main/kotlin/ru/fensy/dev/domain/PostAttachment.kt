package ru.fensy.dev.domain

import java.util.UUID

data class PostAttachment(
    val id: Long,
    val postId: Long,
    val fileId: UUID,
)