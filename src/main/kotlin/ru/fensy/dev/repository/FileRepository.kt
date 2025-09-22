package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository
import ru.fensy.dev.domain.File
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
                INSERT INTO files (id, storage_key, original_filename, mime_type, size_bytes, created_at, updated_at)
                VALUES (:id, :storageKey, :originalFilename, :mimeType, :sizeBytes, :createdAt, :updatedAt)
                RETURNING *
            """.trimIndent()
            )
            .bind("id", file.id)
            .bind("originalFilename", file.originalFileName)
            .bind("storageKey", file.storageKey)
            .bind("mimeType", file.mimeType)
            .bind("sizeBytes", file.sizeBytes)
            .bind("createdAt", file.createdAt)
            .bind("updatedAt", file.updatedAt)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun findByStorageKey(storageKey: String): File? {
        return databaseClient
            .sql("SELECT * FROM files WHERE storage_key = :storageKey")
            .bind("storageKey", storageKey)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    private fun of(source: Map<String, Any?>): File =
        source.let {
            File(
                id = it["id"] as? UUID,
                storageKey = it["storage_key"] as String,
                mimeType = it["mime_type"] as String,
                sizeBytes = it["size_bytes"] as Long,
                originalFileName = it["original_filename"] as String,
                createdAt = it["created_at"] as OffsetDateTime,
                updatedAt = it["updated_at"] as OffsetDateTime,
            )
        }
}