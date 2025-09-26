package ru.fensy.dev.service.media.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import ru.fensy.dev.constants.Constants.DEFAULT_IMAGE_FORMAT
import ru.fensy.dev.constants.Constants.DEFAULT_IMAGE_MIMETYPE
import ru.fensy.dev.domain.*
import ru.fensy.dev.properties.MediaCompressionProperties
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.MediaFileRepository
import ru.fensy.dev.service.media.StorageKeyGenerator
import ru.fensy.dev.service.media.VideoService
import java.time.OffsetDateTime

/**
 * Хендлер для обработки видео постов
 * Обрабатывает только версии: THUMBNAIL, PLAYBACK_720P, LOOP
 */
class PostVideoHandler(
    mediaFileRepository: MediaFileRepository,
    s3FileStorageProxyService: S3FileStorageProxyService,
    storageKeyGenerator: StorageKeyGenerator,
    mediaCompressionProperties: MediaCompressionProperties,
    private val videoService: VideoService,
) : MediaAssetHandler(mediaFileRepository, s3FileStorageProxyService, storageKeyGenerator) {

    private val logger = KotlinLogging.logger {}

    private val compressionSizeConfigs = mapOf(
        MediaFileCompressionSize.THUMBNAIL to mediaCompressionProperties.videoPostAttachment.thumbnail
    )

    override fun getSupportedMediaType(): MediaAssetType = MediaAssetType.VIDEO

    override fun getSupportedPurpose(): MediaAssetPurpose = MediaAssetPurpose.POST_ATTACHMENT

    override fun getSupportedCompressionSizes(): List<MediaFileCompressionSize> {
        return compressionSizeConfigs.keys.toList()
    }

    override suspend fun processVersions(mediaAsset: MediaAsset): List<MediaFile> = coroutineScope {
        val originalFile = mediaFileRepository.findOriginalByMediaAssetId(
            mediaAssetId = mediaAsset.id!!,
        ) ?: throw IllegalArgumentException("Не найден оригинальный файл для медиа-актива ${mediaAsset.id}")

        val processedFilesDeferred = compressionSizeConfigs.map { (compressionSize, sizeConfig) ->
            async {
                try {
                    processVersion(
                        mediaAsset = mediaAsset,
                        originalStorageKey = originalFile.storageKey,
                        originalFileName = originalFile.originalFileName,
                        compressionSize = compressionSize,
                        imageSizeConfig = sizeConfig,
                        createdAt = originalFile.createdAt
                    )
                } catch (e: Exception) {
                    logger.error(e) { "Ошибка при создании сжатой версии $compressionSize для актива ${mediaAsset.id}" }
                    null
                }
            }
        }

        processedFilesDeferred.mapNotNull { it.await() }
    }

    private suspend fun processVersion(
        mediaAsset: MediaAsset,
        originalStorageKey: String,
        originalFileName: String,
        compressionSize: MediaFileCompressionSize,
        imageSizeConfig: MediaCompressionProperties.ImageSizeConfig,
        createdAt: OffsetDateTime
    ): MediaFile {
        val originalStream = s3FileStorageProxyService.downloadFileAsStream(originalStorageKey)
        val thumbnailByteArray = videoService.createThumbnail(originalStream, imageSizeConfig.width, imageSizeConfig.quality)

        val compressedFileName = originalFileName.substringBeforeLast('.') + DEFAULT_IMAGE_FORMAT

        return saveFileFrom(
            mediaAsset = mediaAsset,
            compressionSize = compressionSize,
            originalFileName = compressedFileName,
            mimeType = DEFAULT_IMAGE_MIMETYPE,
            contentLength = thumbnailByteArray.size.toLong(),
            width = 1321,
            height = 132312,
            duration = null,
            byteArray = thumbnailByteArray,
            createdAt = createdAt
        )
    }
}
