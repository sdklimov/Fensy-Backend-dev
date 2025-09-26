package ru.fensy.dev.service.media.handler

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import ru.fensy.dev.domain.*
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.MediaFileRepository
import ru.fensy.dev.service.media.StorageKeyGenerator
import java.time.OffsetDateTime
import java.util.*

/**
 * Абстрактный базовый класс для обработки медиа-активов
 * Содержит общие методы для сохранения обработанных версий в S3 и БД
 */
abstract class MediaAssetHandler(
    protected val mediaFileRepository: MediaFileRepository,
    protected val s3FileStorageProxyService: S3FileStorageProxyService,
    private val storageKeyGenerator: StorageKeyGenerator
) {

    /**
     * Возвращает поддерживаемый тип медиа-актива
     */
    abstract fun getSupportedMediaType(): MediaAssetType

    /**
     * Возвращает поддерживаемую цель медиа-актива
     */
    abstract fun getSupportedPurpose(): MediaAssetPurpose

    /**
     * Возвращает список размеров сжатия, которые должны быть созданы для данного типа актива
     */
    abstract fun getSupportedCompressionSizes(): List<MediaFileCompressionSize>

    /**
     * Обрабатывает версии медиа-актива (сжатие, превью и т.д.)
     * Оригинальный файл уже сохранен в MediaAssetService
     */
    abstract suspend fun processVersions(mediaAsset: MediaAsset): List<MediaFile>

    /**
     * Проверяет, может ли данный хендлер обработать медиа-актив
     */
    fun canHandle(mediaAsset: MediaAsset): Boolean {
        return mediaAsset.mediaType == getSupportedMediaType() &&
                mediaAsset.purpose == getSupportedPurpose()
    }


    /**
     * Общий метод для сохранения файла из массива байтов
     */
    protected suspend fun saveFileFrom(
        mediaAsset: MediaAsset,
        compressionSize: MediaFileCompressionSize,
        originalFileName: String,
        mimeType: String,
        byteArray: ByteArray,
        contentLength: Long,
        width: Int?,
        height: Int?,
        duration: Int?,
        createdAt: OffsetDateTime
    ): MediaFile {
        val storageKey = generateStorageKey(mediaAsset, compressionSize, originalFileName, createdAt)
        s3FileStorageProxyService.uploadFile(mimeType, contentLength, byteArray, originalFileName, storageKey)

        val mediaFile = MediaFile(
            id = UUID.randomUUID(),
            mediaAssetId = mediaAsset.id!!,
            compressionSize = compressionSize,
            storageKey = storageKey,
            originalFileName = originalFileName,
            mimeType = mimeType,
            sizeBytes = contentLength,
            width = width,
            height = height,
            duration = duration,
            createdAt = createdAt,
            updatedAt = createdAt
        )

        return mediaFileRepository.create(mediaFile)
    }

    protected suspend fun saveFileFrom(
        mediaAsset: MediaAsset,
        compressionSize: MediaFileCompressionSize,
        originalFileName: String,
        mimeType: String,
        flow: Flow<DataBuffer>,
        contentLength: Long,
        width: Int?,
        height: Int?,
        duration: Int?,
        createdAt: OffsetDateTime
    ): MediaFile {
        val storageKey = generateStorageKey(mediaAsset, compressionSize, originalFileName, createdAt)
        s3FileStorageProxyService.uploadFile(mimeType, contentLength, flow, originalFileName, storageKey)

        val mediaFile = MediaFile(
            id = UUID.randomUUID(),
            mediaAssetId = mediaAsset.id!!,
            compressionSize = compressionSize,
            storageKey = storageKey,
            originalFileName = originalFileName,
            mimeType = mimeType,
            sizeBytes = contentLength,
            width = width,
            height = height,
            duration = duration,
            createdAt = createdAt,
            updatedAt = createdAt
        )

        return mediaFileRepository.create(mediaFile)
    }

    private fun generateStorageKey(
        mediaAsset: MediaAsset,
        compressionSize: MediaFileCompressionSize,
        originalFileName: String,
        dateTime: OffsetDateTime
    ): String {
        return storageKeyGenerator.generateStorageKey(
            userId = mediaAsset.createdBy,
            dateTime = dateTime,
            mediaAssetId = mediaAsset.id!!,
            compressionSize = compressionSize,
            originalFileName = originalFileName,
            purpose = mediaAsset.purpose
        )
    }
}
