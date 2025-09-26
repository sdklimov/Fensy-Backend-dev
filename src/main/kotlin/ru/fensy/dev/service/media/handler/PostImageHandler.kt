package ru.fensy.dev.service.media.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import ru.fensy.dev.constants.Constants
import ru.fensy.dev.constants.Constants.DEFAULT_IMAGE_FORMAT
import ru.fensy.dev.constants.Constants.DEFAULT_IMAGE_MIMETYPE
import ru.fensy.dev.domain.MediaAsset
import ru.fensy.dev.domain.MediaAssetPurpose
import ru.fensy.dev.domain.MediaAssetType
import ru.fensy.dev.domain.MediaFile
import ru.fensy.dev.domain.MediaFileCompressionSize
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.MediaFileRepository
import ru.fensy.dev.service.media.StorageKeyGenerator
import ru.fensy.dev.service.media.ImageService
import ru.fensy.dev.properties.MediaCompressionProperties
import java.time.OffsetDateTime

/**
 * Хендлер для обработки изображений постов
 * Обрабатывает только версии: LARGE, MEDIUM, THUMBNAIL
 * Оригинал сохраняется в MediaAssetService
 */
@Component
class PostImageHandler(
    mediaFileRepository: MediaFileRepository,
    s3FileStorageProxyService: S3FileStorageProxyService,
    storageKeyGenerator: StorageKeyGenerator,
    mediaCompressionProperties: MediaCompressionProperties,
    private val imageService: ImageService,
) : MediaAssetHandler(mediaFileRepository, s3FileStorageProxyService, storageKeyGenerator) {

    private val logger = KotlinLogging.logger {}

    private val compressionSizeConfigs = mapOf(
        MediaFileCompressionSize.LARGE to mediaCompressionProperties.imagePostAttachment.large,
        MediaFileCompressionSize.MEDIUM to mediaCompressionProperties.imagePostAttachment.medium,
        MediaFileCompressionSize.THUMBNAIL to mediaCompressionProperties.imagePostAttachment.thumbnail
    )

    override fun getSupportedMediaType(): MediaAssetType = MediaAssetType.IMAGE

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

    /**
     * Обрабатывает одну версию изображения: скачивает поток → сжимает → сохраняет поток в S3 → сохраняет в БД
     */
    private suspend fun processVersion(
        mediaAsset: MediaAsset,
        originalStorageKey: String,
        originalFileName: String,
        compressionSize: MediaFileCompressionSize,
        imageSizeConfig: MediaCompressionProperties.ImageSizeConfig,
        createdAt: OffsetDateTime
    ): MediaFile? {
        val originalStream = s3FileStorageProxyService.downloadFileAsStream(originalStorageKey)
        val compressedResult = imageService.compressImageStream(originalStream, imageSizeConfig)
            ?: return null

        val compressedFileName = originalFileName.substringBeforeLast('.') + DEFAULT_IMAGE_FORMAT

        return saveFileFrom(
            mediaAsset = mediaAsset,
            compressionSize = compressionSize,
            originalFileName = compressedFileName,
            mimeType = DEFAULT_IMAGE_MIMETYPE,
            byteArray = compressedResult.byteArray,
            contentLength = compressedResult.sizeBytes,
            width = compressedResult.width,
            height = compressedResult.height,
            duration = null,
            createdAt = createdAt
        )
    }
}
