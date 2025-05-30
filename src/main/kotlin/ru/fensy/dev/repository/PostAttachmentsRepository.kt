package ru.fensy.dev.repository

import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.PostAttachment

/**
 * Репозиторий post_attachments
 */
@Component
class PostAttachmentsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findByPostId(postId: Long): List<PostAttachment> =
        databaseClient
            .sql(
                """
                select * from post_attachments where post_id = :postId
            """.trimIndent()
            )
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    private fun of(source: Map<String, Any>) = source.let {
        PostAttachment(
            id = it["id"] as Long,
            postId = it["post_id"] as Long,
            fileId = it["file_id"] as UUID,
        )
    }

}
