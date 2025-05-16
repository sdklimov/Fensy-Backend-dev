package ru.fensy.dev.domain

import java.time.LocalDateTime
import java.time.OffsetDateTime

/**
 * Пост
 */
data class Post(
    val id: Long? = null,
    val originalPostId: Long? = null,
    val isRepost: Boolean?,
    val authorId: Long,
    val title: String?,
    val content: String?,
    val allowViewingFor: String,
    val pinned: Boolean?,
    val adultContent: Boolean?,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
