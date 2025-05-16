package ru.fensy.dev.repository

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Post
import java.time.OffsetDateTime

@Component
class PostRepository(
    private val databaseClient: DatabaseClient,
    private val objectMapper: ObjectMapper,
) {

    suspend fun create(post: Post): Post {
        return databaseClient
            .sql {
                """
                insert into posts (original_post_id, is_repost, author_id, title, content, allow_viewing_for, pinned, adult_content)
                values (:originalPostId, :isReport, :authorId, :title, :content, :allowViewingFor, :pinned, :adultContent)
                returning *
            """.trimIndent()
            }
            .bind("originalPostId", post.originalPostId)
            .bind("isReport", post.isRepost)
            .bind("authorId", post.authorId)
            .bind("title", post.title)
            .bind("content", post.content)
            .bind("allowViewingFor", post.allowViewingFor)
            .bind("pinned", post.pinned)
            .bind("adultContent", post.adultContent)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>): Post {
        return source.let {
            Post(
                id = it["id"] as Long,
                originalPostId = it["original_post_id"] as? Long,
                isRepost = it["is_repost"] as Boolean,
                authorId = it["author_id"] as Long,
                title = it["title"] as String,
                content = it["content"] as String,
                allowViewingFor = it["allow_viewing_for"] as String,
                pinned = it["pinned"] as Boolean,
                adultContent = it["adult_content"] as Boolean,
                createdAt = it["created_at"] as OffsetDateTime,
                updatedAt = it["updated_at"] as OffsetDateTime

            )
        }
    }

}