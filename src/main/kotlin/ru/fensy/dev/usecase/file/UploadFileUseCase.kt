package ru.fensy.dev.usecase.file

import java.util.UUID
import kotlinx.coroutines.coroutineScope
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import ru.fensy.dev.domain.File
import ru.fensy.dev.exception.FileUploadSessionNotExistsException
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.FileRepository
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.rest.UploadFileResponse
import ru.fensy.dev.usecase.BaseUseCase
import java.time.OffsetDateTime

@Component
@Transactional
class UploadFileUseCase(
    private val s3FileStorageProxyService: S3FileStorageProxyService,
    private val fileUploadSessionRepository: FileUploadSessionRepository,
    private val fileRepository: FileRepository,
) : BaseUseCase() {

    suspend fun execute(
        sessionId: UUID,
        contentType: String,
        contentLength: Long,
        file: Flux<DataBuffer>,
        fileName: String,
    ): UploadFileResponse = coroutineScope {
        fileUploadSessionRepository.getActiveSessionByIdAndUserId(sessionId, currentUser(required = true)!!.id!!)
            ?: throw FileUploadSessionNotExistsException("Сессия не обнаружена")

        val storageKey = UUID.randomUUID()
        val createdFile = fileRepository.create(
            File(
                id = storageKey,
                storageKey = storageKey.toString(),
                sizeBytes = contentLength,
                mimeType = contentType,
                originalFileName = fileName,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
        )
        s3FileStorageProxyService.uploadFile(contentType, contentLength, file, fileName, createdFile.id.toString())

        fileUploadSessionRepository.addFileToSession(sessionId, createdFile.id!!)

        return@coroutineScope UploadFileResponse(storageKey)
    }

}
