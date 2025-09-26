package ru.fensy.dev.service.media

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalEventPublisher
import reactor.core.publisher.Flux
import ru.fensy.dev.domain.*
import ru.fensy.dev.event.MediaAssetCreatedEvent
import ru.fensy.dev.extensions.determineAssetType
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.MediaAssetRepository
import ru.fensy.dev.repository.MediaFileRepository
import java.time.OffsetDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Сервис для сохранения медиа-активов
 */
@Service
@Transactional
class MediaAssetService(
    private val mediaAssetRepository: MediaAssetRepository,
    private val mediaFileRepository: MediaFileRepository,
    private val s3FileStorageProxyService: S3FileStorageProxyService,
    private val storageKeyGenerator: StorageKeyGenerator,
    private val eventPublisher: TransactionalEventPublisher
) {

    /**
     * Создать медиа-актив из потока данных с сохранением только оригинала
     */
    @Transactional
    suspend fun createMediaAssetFromStream(
        name: String,
        contentType: String,
        purpose: MediaAssetPurpose,
        createdBy: Long,
        originalFileName: String,
        mimeType: String,
        contentLength: Long,
        fileStream: Flow<DataBuffer>,
        width: Int? = null,
        height: Int? = null,
        duration: Int? = null
    ): MediaAsset {
        val now = OffsetDateTime.now()
        val assetId = UUID.randomUUID()
        val mediaType = contentType.determineAssetType()

        val mediaAsset = createMediaAssetEntity(assetId, name, mediaType, purpose, createdBy, now)

        val mediaFile = saveOriginalFile(
            mediaAsset = mediaAsset,
            originalFileName = originalFileName,
            mimeType = mimeType,
            contentLength = contentLength,
            fileStream = fileStream,
            width = width,
            height = height,
            duration = duration,
            createdAt = now
        )

        eventPublisher
            .publishEvent(MediaAssetCreatedEvent(mediaAsset.id!!, mediaFile.storageKey))
            .awaitSingleOrNull()

        return mediaAsset
    }

    /**
     * Сохранить оригинальный файл - универсальная операция для всех типов медиа
     */
    private suspend fun saveOriginalFile(
        mediaAsset: MediaAsset,
        originalFileName: String,
        mimeType: String,
        contentLength: Long,
        fileStream: Flow<DataBuffer>,
        width: Int?,
        height: Int?,
        duration: Int?,
        createdAt: OffsetDateTime
    ): MediaFile {
        val storageKey = generateStorageKey(mediaAsset, originalFileName, createdAt)
        s3FileStorageProxyService.uploadFile(mimeType, contentLength, fileStream, originalFileName, storageKey)

        val mediaFile = MediaFile(
            id = UUID.randomUUID(),
            mediaAssetId = mediaAsset.id!!,
            compressionSize = MediaFileCompressionSize.ORIGINAL,
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
        originalFileName: String,
        dateTime: OffsetDateTime
    ): String {
        return storageKeyGenerator.generateStorageKey(
            userId = mediaAsset.createdBy,
            dateTime = dateTime,
            mediaAssetId = mediaAsset.id!!,
            compressionSize = MediaFileCompressionSize.ORIGINAL,
            originalFileName = originalFileName,
            purpose = mediaAsset.purpose
        )
    }

    private suspend fun createMediaAssetEntity(
        assetId: UUID,
        name: String,
        mediaType: MediaAssetType,
        purpose: MediaAssetPurpose,
        createdBy: Long,
        now: OffsetDateTime
    ): MediaAsset {
        return mediaAssetRepository.create(
            MediaAsset(
                id = assetId,
                name = name,
                mediaType = mediaType,
                purpose = purpose,
                createdBy = createdBy,
                createdAt = now,
                updatedAt = now
            )
        )
    }
}
