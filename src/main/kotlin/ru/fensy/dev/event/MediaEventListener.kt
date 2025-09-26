package ru.fensy.dev.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.MediaAssetRepository
import ru.fensy.dev.service.media.handler.MediaAssetHandler
import java.util.*

data class MediaAssetCreatedEvent(val mediaAssetId: UUID, val originalFileStorageKey: String)

@Component
class MediaEventListener(
    @Qualifier("applicationScope") private val applicationScope: CoroutineScope,
    private val handlers: MutableList<out MediaAssetHandler>,
    private val mediaAssetRepository: MediaAssetRepository,
    private val s3FileStorageProxyService: S3FileStorageProxyService
) {

    /**
     * Создание сжатых вариантов asset
     */
    @TransactionalEventListener
    fun handleMediaAssetCreatedAfterCommit(event: MediaAssetCreatedEvent) {
        applicationScope.launch {
            val mediaAsset = mediaAssetRepository.findById(event.mediaAssetId)!!
            val handler = handlers.firstOrNull { it.canHandle(mediaAsset) } ?: return@launch
            handler.processVersions(mediaAsset)
        }
    }

    /**
     * Обработчик fallback - удаляет медиа-актив, если транзакция, в рамках которой он был создан, откатилась
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handleMediaAssetCreatedAfterRollback(event: MediaAssetCreatedEvent) {
        applicationScope.launch {
            s3FileStorageProxyService.deleteFile(event.originalFileStorageKey)
        }
    }
}