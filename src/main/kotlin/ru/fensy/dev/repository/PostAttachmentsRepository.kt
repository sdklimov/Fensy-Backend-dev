package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.MediaAssetType
import ru.fensy.dev.graphql.responses.ImagePostAttachment
import ru.fensy.dev.graphql.responses.PostAttachment
import ru.fensy.dev.graphql.responses.VideoPostAttachment
import java.util.UUID

/**
 * Репозиторий post_attachments
 */
@Component
class PostAttachmentsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findByPostIdVideosAssetType(postId: Long): List<VideoPostAttachment> =
        databaseClient
            .sql(
                """
                select ma.* from post_attachments pa
                join media_assets ma on ma.id = pa.file_id and post_id = :postId
                where ma.asset_type = :assetType
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("assetType", MediaAssetType.VIDEO.name)
            .fetch()
            .all()
            .map {
                VideoPostAttachment(
                    assetId = it["id"] as UUID,
                    playback = null
                )
            }
            .collectList()
            .awaitSingle()

    suspend fun findByPostIdImagesAssetType(postId: Long): List<ImagePostAttachment> =
        databaseClient
            .sql(
                """
                select ma.* from post_attachments pa
                join media_assets ma on ma.id = pa.file_id and post_id = :postId
                where ma.asset_type = :assetType
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("assetType", MediaAssetType.IMAGE.name)
            .fetch()
            .all()
            .map {
                ImagePostAttachment(
                    assetId = it["id"] as UUID,
                    thumbnail = null,
                    medium = null,
                    large = null
                )
            }
            .collectList()
            .awaitSingle()


}
