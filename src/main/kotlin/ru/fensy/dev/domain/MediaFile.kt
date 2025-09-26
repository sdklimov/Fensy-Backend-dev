package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.*

/**
 * Медиа-файл - физическая версия файла, принадлежащая медиа-активу
 */
data class MediaFile(
    val id: UUID? = null,
    val mediaAssetId: UUID,
    val compressionSize: MediaFileCompressionSize,
    val storageKey: String,
    val originalFileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val width: Int? = null, // для изображений и видео
    val height: Int? = null, // для изображений и видео
    val duration: Int? = null, // для видео и аудио в секундах
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

enum class MediaFileCompressionSize {
    ORIGINAL,
    LARGE,
    MEDIUM,
    THUMBNAIL,
    LOOP,
    PLAYBACK_720P
}