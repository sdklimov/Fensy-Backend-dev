package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository
import ru.fensy.dev.domain.File
import ru.fensy.dev.domain.FileContextType
import java.time.OffsetDateTime
import java.util.*

@Repository
class FileRepository(
    private val databaseClient: DatabaseClient,
) {
    suspend fun findById(id: UUID): File? {
        return databaseClient
            .sql("SELECT * FROM files WHERE id = :id")
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun create(file: File): File {
        return databaseClient
            .sql(
                """
                INSERT INTO files (id, s3_key, context_id, context_type)
                VALUES (:id, :s3Key, :contextId, :contextType)
                RETURNING *
            """.trimIndent()
            )
            .bind("id", file.id)
            .bind("s3Key", file.s3Key)
            .bind("contextId", file.contextId)
            .bind("contextType", file.contextType.name)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun findByS3Key(s3Key: String): File? {
        return databaseClient
            .sql("SELECT * FROM files WHERE s3_key = :s3Key")
            .bind("s3Key", s3Key)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun findAllByContextType(contextType: FileContextType): List<File> {
        return databaseClient
            .sql("SELECT * FROM files WHERE context_type = :contextType")
            .bind("contextType", contextType.name)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    private fun of(source: Map<String, Any?>): File =
        source.let {
            File(
                id = it["id"] as? UUID,
                contextId = it["context_id"] as? String,
                contextType = FileContextType.valueOf(it["context_type"] as String),
                s3Key = it["s3_key"] as String,
                createdAt = it["created_at"] as OffsetDateTime,
                updatedAt = it["updated_at"] as OffsetDateTime,
            )
        }
}