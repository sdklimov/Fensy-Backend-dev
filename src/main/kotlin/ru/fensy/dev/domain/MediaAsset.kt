package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

/**
 * Медиа-актив - логический контейнер для файлов
 */
data class MediaAsset(
    val id: UUID? = null,
    val name: String,
    val mediaType: MediaAssetType,
    val purpose: MediaAssetPurpose,
    val createdBy: Long,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

enum class MediaAssetType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    OTHER
}


enum class MediaAssetPurpose {
    AVATAR,
    POST_ATTACHMENT
}
