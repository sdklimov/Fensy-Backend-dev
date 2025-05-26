package ru.fensy.dev.repository

import java.util.UUID
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

@Component
class PostAttachmentRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun savePostAttachment(postId: Long, fileId: UUID) {
        databaseClient
            .sql(
                """
                insert into post_attachments (post_id, file_path) values (:postId, :fileId)
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("fileId", fileId)
            .fetch()
            .awaitRowsUpdated()
    }
}
