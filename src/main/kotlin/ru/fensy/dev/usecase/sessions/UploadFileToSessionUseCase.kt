package ru.fensy.dev.usecase.sessions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import ru.fensy.dev.domain.MediaAssetPurpose
import ru.fensy.dev.exception.FileUploadSessionNotExistsException
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.rest.sessions.UploadFileResponse
import ru.fensy.dev.service.media.MediaAssetService
import ru.fensy.dev.usecase.BaseUseCase
import java.util.*

@Component
@Transactional
class UploadFileToSessionUseCase(
    private val fileUploadSessionRepository: FileUploadSessionRepository,
    private val mediaAssetService: MediaAssetService,
) : BaseUseCase() {

    suspend fun execute(
        sessionId: UUID,
        contentType: String,
        contentLength: Long,
        file: Flow<DataBuffer>,
        fileName: String,
    ): UploadFileResponse {
        val currentUserId = currentUser(required = true)!!.id!!

        fileUploadSessionRepository.getActiveSessionByIdAndUserId(sessionId, currentUserId)
            ?: throw FileUploadSessionNotExistsException("Сессия не обнаружена")

        val mediaAsset = mediaAssetService.createMediaAssetFromStream(
            name = fileName,
            purpose = MediaAssetPurpose.POST_ATTACHMENT,
            contentType = contentType,
            createdBy = currentUserId,
            originalFileName = fileName,
            mimeType = contentType,
            contentLength = contentLength,
            fileStream = file
        )

        fileUploadSessionRepository.addFileToSession(sessionId, mediaAsset.id!!)
        return UploadFileResponse(mediaAsset.id)
    }


}
