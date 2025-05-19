package ru.fensy.dev.domain

import java.time.OffsetDateTime

/**
 * Пост
 */
data class Post(
    val id: Long,
    val authorId: Long,
    val title: String?,
    val content: String,
    val allowViewingFor: PostAllowVieweingFor,
    val pinned: Boolean,
    val adultContent: Boolean = false,
    val originalPostId: Long? = null,
    val isRepost: Boolean = false,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
