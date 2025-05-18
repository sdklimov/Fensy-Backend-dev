package ru.fensy.dev.repository

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.PostAllowVieweingFor
import java.time.OffsetDateTime

@Component
@Transactional
class PostRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findById(id: Long): Post {
        return databaseClient
            .sql {
                """
                select * from posts where id = :id
            """.trimIndent()
            }
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun findByAuthorId(id: Long): List<Post> {
        return databaseClient
            .sql {
                """
                select * from posts where author_id = :authorId
            """.trimIndent()
            }
            .bind("authorId", id)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>): Post {
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