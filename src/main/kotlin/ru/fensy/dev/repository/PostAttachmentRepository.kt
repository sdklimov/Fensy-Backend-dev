package ru.fensy.dev.repository

import java.util.UUID
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

@Component
class PostAttachmentRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun savePostAttachments(postId: Long, fileIds: List<UUID>) {
        val values = fileIds.joinToString(", ") { "($postId, '$it')" }
        databaseClient
            .sql(
                """
                insert into post_attachments (post_id, file_id) values $values
            """.trimIndent()
            )
            .fetch()
            .awaitRowsUpdated()
    }
}
