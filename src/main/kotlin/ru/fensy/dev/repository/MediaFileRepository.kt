package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository
import ru.fensy.dev.domain.MediaFile
import ru.fensy.dev.domain.MediaFileCompressionSize
import java.time.OffsetDateTime
import java.util.*

@Repository
class MediaFileRepository(
    private val databaseClient: DatabaseClient,
) {
    suspend fun findById(id: UUID): MediaFile? {
        return databaseClient
            .sql("SELECT * FROM media_files WHERE id = :id")
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun findByStorageKey(storageKey: String): MediaFile? {
        return databaseClient
            .sql("SELECT * FROM media_files WHERE storage_key = :storageKey")
            .bind("storageKey", storageKey)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun findByMediaAssetId(mediaAssetId: UUID): List<MediaFile> {
        return databaseClient
            .sql("SELECT * FROM media_files WHERE media_asset_id = :mediaAssetId ORDER BY created_at")
            .bind("mediaAssetId", mediaAssetId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun findByMediaAssetIdAndCompressionSize(mediaAssetId: UUID, compressionSize: MediaFileCompressionSize): MediaFile? {
        return databaseClient
            .sql("SELECT * FROM media_files WHERE media_asset_id = :mediaAssetId AND compression_size = :compressionSize")
            .bind("mediaAssetId", mediaAssetId)
            .bind("compressionSize", compressionSize.name)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun findOriginalByMediaAssetId(mediaAssetId: UUID): MediaFile? {
        return findByMediaAssetIdAndCompressionSize(mediaAssetId, MediaFileCompressionSize.ORIGINAL)
    }

    suspend fun create(file: MediaFile): MediaFile {
        return databaseClient
            .sql(
                """
                INSERT INTO media_files (id, media_asset_id, compression_size, storage_key, original_filename, 
                                       mime_type, size_bytes, width, height, duration, created_at, updated_at)
                VALUES (:id, :mediaAssetId, :compressionSize, :storageKey, :originalFilename, :mimeType, 
                        :sizeBytes, :width, :height, :duration, :createdAt, :updatedAt)
                RETURNING *
                """.trimIndent()
            )
            .bind("id", file.id)
            .bind("mediaAssetId", file.mediaAssetId)
            .bind("compressionSize", file.compressionSize.name)
            .bind("storageKey", file.storageKey)
            .bind("originalFilename", file.originalFileName)
            .bind("mimeType", file.mimeType)
            .bind("sizeBytes", file.sizeBytes)
            .bind("width", file.width)
            .bind("height", file.height)
            .bind("duration", file.duration)
            .bind("createdAt", file.createdAt)
            .bind("updatedAt", file.updatedAt)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun update(file: MediaFile): MediaFile {
        return databaseClient
            .sql(
                """
                UPDATE media_files 
                SET compression_size = :compressionSize, storage_key = :storageKey, original_filename = :originalFilename,
                    mime_type = :mimeType, size_bytes = :sizeBytes, width = :width, height = :height,
                    duration = :duration, updated_at = :updatedAt
                WHERE id = :id
                RETURNING *
                """.trimIndent()
            )
            .bind("id", file.id)
            .bind("compressionSize", file.compressionSize.name)
            .bind("storageKey", file.storageKey)
            .bind("originalFilename", file.originalFileName)
            .bind("mimeType", file.mimeType)
            .bind("sizeBytes", file.sizeBytes)
            .bind("width", file.width)
            .bind("height", file.height)
            .bind("duration", file.duration)
            .bind("updatedAt", file.updatedAt)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun deleteById(id: UUID): Boolean {
        val rowsAffected = databaseClient
            .sql("DELETE FROM media_files WHERE id = :id")
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
        return rowsAffected > 0
    }

    private fun of(source: Map<String, Any?>): MediaFile =
        source.let {
            MediaFile(
                id = it["id"] as? UUID,
                mediaAssetId = it["media_asset_id"] as UUID,
                compressionSize = MediaFileCompressionSize.valueOf(it["compression_size"] as String),
                storageKey = it["storage_key"] as String,
                originalFileName = it["original_filename"] as String,
                mimeType = it["mime_type"] as String,
                sizeBytes = it["size_bytes"] as Long,
                width = it["width"] as? Int,
                height = it["height"] as? Int,
                duration = it["duration"] as? Int,
                createdAt = it["created_at"] as OffsetDateTime,
                updatedAt = it["updated_at"] as OffsetDateTime,
            )
        }
}
