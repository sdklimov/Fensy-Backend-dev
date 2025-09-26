package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository
import ru.fensy.dev.domain.MediaAsset
import ru.fensy.dev.domain.MediaAssetType
import ru.fensy.dev.domain.MediaAssetPurpose
import java.time.OffsetDateTime
import java.util.*

@Repository
class MediaAssetRepository(
    private val databaseClient: DatabaseClient,
) {
    suspend fun findById(id: UUID): MediaAsset? {
        return databaseClient
            .sql("SELECT * FROM media_assets WHERE id = :id")
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun findByIdAndCreatedBy(id: UUID, createdBy: Long): MediaAsset? {
        return databaseClient
            .sql("SELECT * FROM media_assets WHERE id = :id AND created_by = :createdBy")
            .bind("id", id)
            .bind("createdBy", createdBy)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun create(asset: MediaAsset): MediaAsset {
        return databaseClient
            .sql(
                """
                INSERT INTO media_assets (id, name, asset_type, purpose, created_by, created_at, updated_at)
                VALUES (:id, :name, :assetType, :purpose, :createdBy, :createdAt, :updatedAt)
                RETURNING *
                """.trimIndent()
            )
            .bind("id", asset.id)
            .bind("name", asset.name)
            .bind("assetType", asset.mediaType.name)
            .bind("purpose", asset.purpose.name)
            .bind("createdBy", asset.createdBy)
            .bind("createdAt", asset.createdAt)
            .bind("updatedAt", asset.updatedAt)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun update(asset: MediaAsset): MediaAsset {
        return databaseClient
            .sql(
                """
                UPDATE media_assets 
                SET name = :name, asset_type = :assetType, purpose = :purpose, updated_at = :updatedAt
                WHERE id = :id
                RETURNING *
                """.trimIndent()
            )
            .bind("id", asset.id)
            .bind("name", asset.name)
            .bind("assetType", asset.mediaType.name)
            .bind("purpose", asset.purpose.name)
            .bind("updatedAt", asset.updatedAt)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun findByCreatedBy(createdBy: Long): List<MediaAsset> {
        return databaseClient
            .sql("SELECT * FROM media_assets WHERE created_by = :createdBy ORDER BY created_at DESC")
            .bind("createdBy", createdBy)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun findByAssetType(assetType: MediaAssetType): List<MediaAsset> {
        return databaseClient
            .sql("SELECT * FROM media_assets WHERE asset_type = :assetType ORDER BY created_at DESC")
            .bind("assetType", assetType.name)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun deleteById(id: UUID): Boolean {
        val rowsAffected = databaseClient
            .sql("DELETE FROM media_assets WHERE id = :id")
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
        return rowsAffected > 0
    }

    private fun of(source: Map<String, Any?>): MediaAsset =
        source.let {
            MediaAsset(
                id = it["id"] as? UUID,
                name = it["name"] as String,
                mediaType = MediaAssetType.valueOf(it["asset_type"] as String),
                purpose = MediaAssetPurpose.valueOf(it["purpose"] as String),
                createdBy = it["created_by"] as Long,
                createdAt = it["created_at"] as OffsetDateTime,
                updatedAt = it["updated_at"] as OffsetDateTime,
            )
        }
}
