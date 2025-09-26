package ru.fensy.dev.service.media

import org.springframework.stereotype.Component
import ru.fensy.dev.domain.MediaAssetPurpose
import ru.fensy.dev.domain.MediaFileCompressionSize
import java.time.OffsetDateTime
import java.util.*

/**
 * Утилитный сервис для генерации структурированных ключей S3
 */
@Component
class StorageKeyGenerator {

    /**
     * Генерирует структурированный ключ для хранения в S3 на основе назначения медиа-актива
     * Формат для аватаров: users/<user_id>/avatars/<media_asset_id>/<compression_size>.<расширение>
     * Формат для вложений постов: users/<user_id>/uploads/<год>/<месяц>/<media_asset_id>/<compression_size>.<расширение>
     */
    fun generateStorageKey(
        userId: Long,
        dateTime: OffsetDateTime,
        mediaAssetId: UUID,
        compressionSize: MediaFileCompressionSize,
        originalFileName: String,
        purpose: MediaAssetPurpose
    ): String {
        val fileExtension = getFileExtension(originalFileName)
        val compressionSizeName = compressionSize.name.lowercase()

        return when (purpose) {
            MediaAssetPurpose.AVATAR -> {
                "users/$userId/avatars/$mediaAssetId/$compressionSizeName.$fileExtension"
            }
            MediaAssetPurpose.POST_ATTACHMENT -> {
                val year = dateTime.year
                val month = String.format("%02d", dateTime.monthValue)
                "users/$userId/uploads/$year/$month/$mediaAssetId/$compressionSizeName.$fileExtension"
            }
        }
    }

    /**
     * Извлекает расширение файла из имени файла
     */
    private fun getFileExtension(fileName: String): String {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1).lowercase()
        } else {
            "bin" // дефолтное расширение для файлов без расширения
        }
    }
}
