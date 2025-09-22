package ru.fensy.dev.usecase.file

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.exception.FileUploadSessionNotExistsException
import ru.fensy.dev.exception.SessionAlreadyClosedException
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.usecase.BaseUseCase
import java.util.UUID

@Component
@Transactional
class RemoveFileFromSessionUseCase(
    private val fileUploadSessionRepository: FileUploadSessionRepository,
) : BaseUseCase() {
    suspend fun execute(
        sessionId: UUID,
        fileId: UUID
    ) {
        val session = fileUploadSessionRepository.getActiveSessionByIdAndUserId(sessionId, currentUser(required = true)!!.id!!)
            ?: throw FileUploadSessionNotExistsException("Сессия не обнаружена")
        if (session.isClosed) {
            throw SessionAlreadyClosedException("Сессия закрыта")
        } else {
            fileUploadSessionRepository.removeFileFromSession(sessionId, fileId)
        }
    }
}