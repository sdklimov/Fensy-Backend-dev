package ru.fensy.dev.usecase.file

import java.util.UUID
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import ru.fensy.dev.exception.FileUploadSessionNotExiststsException
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.rest.UploadFileResponse
import ru.fensy.dev.usecase.BaseUseCase

@Component
class UploadFileUseCase(
    private val s3FileStorageProxyService: S3FileStorageProxyService,
    private val fileUploadSessionRepository: FileUploadSessionRepository,
) : BaseUseCase() {

    suspend fun execute(sessionId: UUID, contentType: String, contentLength: Long, file: Flux<DataBuffer>): UploadFileResponse {
        fileUploadSessionRepository.getByIdAndUserId(sessionId, currentUser(required = true)!!.id!!)
            ?: throw FileUploadSessionNotExiststsException("Сессия не обнаружена")

        val fileId = s3FileStorageProxyService.uploadFile(contentType, contentLength, file)

        fileUploadSessionRepository.addFileToSession(sessionId, fileId)
        return UploadFileResponse(fileId)
    }

}
