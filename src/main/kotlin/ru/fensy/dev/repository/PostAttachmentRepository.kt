package ru.fensy.dev.repository

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

@Component
class PostAttachmentRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun savePostAttachment(postId: Long, filePath: String) {
        databaseClient
            .sql(
                """
                insert into post_attachments (post_id, file_path) values (:postId, :filePath)
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("filePath", filePath)
            .fetch()
            .awaitRowsUpdated()
    }

}
