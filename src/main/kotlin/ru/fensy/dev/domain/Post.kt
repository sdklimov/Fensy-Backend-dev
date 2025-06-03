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
) {

    companion object {
        fun of(source: Map<String, Any>): Post {
            return source.let {
                Post(
                    id = it["id"] as Long,
                    authorId = it["author_id"] as Long,
                    title = it["title"] as? String,
                    content = it["content"] as String,
                    allowViewingFor = PostAllowVieweingFor.valueOf(it["allow_viewing_for"] as String),
                    pinned = it["pinned"] as Boolean,
                    adultContent = it["adult_content"] as Boolean,
                    originalPostId = it["original_post_id"] as? Long,
                    isRepost = it["is_repost"] as Boolean,
                    createdAt = it["created_at"] as OffsetDateTime,
                    updatedAt = it["updated_at"] as OffsetDateTime,
                )
            }
        }
    }


}
