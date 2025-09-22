package ru.fensy.dev.service.avatar

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import ru.fensy.dev.domain.File
import ru.fensy.dev.repository.FileRepository
import java.time.OffsetDateTime
import java.util.*

/**
 * Сервис стандартных аватаров
 */
@Service
class DefaultAvatarService(
    @Value("\${application.avatar.default-avatar-s3keys}")
    private val defaultAvatarsS3Keys: Set<String>,
    private val fileRepository: FileRepository
) {

    private val logger = KotlinLogging.logger {}
    private var defaultAvatarIds: List<UUID> = emptyList()

    /**
     * Инициализация дефолтных аватарок при запуске приложения
     */
    @EventListener(ApplicationReadyEvent::class)
    fun initDefaultAvatars() {
        runBlocking {
            logger.info { "Инициализация дефолтных аватарок. Всего ключей: ${defaultAvatarsS3Keys.size}" }

            val avatarIds = mutableListOf<UUID>()

            for (storageKey in defaultAvatarsS3Keys) {
                val existingFile = fileRepository.findByStorageKey(storageKey)

                if (existingFile != null) {
                    logger.debug { "Файл с ключом $storageKey уже существует, используем ID: ${existingFile.id}" }
                    existingFile.id?.let { avatarIds.add(it) }
                } else {
                    val newFile = fileRepository.create(
                        File(
                            id = UUID.randomUUID(),
                            originalFileName = "default_avatar.png",
                            storageKey = storageKey,
                            mimeType = "image/png",
                            sizeBytes = 0L, // Размер неизвестен на этапе инициализации
                            createdAt = OffsetDateTime.now(),
                            updatedAt = OffsetDateTime.now()
                        ) //TODO сделать получше
                    )
                    newFile.id?.let { avatarIds.add(it) }
                }
            }

            defaultAvatarIds = avatarIds
            logger.info { "Инициализация дефолтных аватарок завершена. Доступно ${defaultAvatarIds.size} аватарок" }
        }
    }

    /**
     * Получить случайную аватарку из дефолтных
     */
    suspend fun getRandomAvatar(): File {
        if (defaultAvatarIds.isEmpty()) {
            throw IllegalStateException("Нет доступных дефолтных аватарок")
        }

        val randomId = defaultAvatarIds.random()
        return fileRepository.findById(randomId)
            ?: throw IllegalStateException("Файл с ID $randomId не найден")
    }
}
