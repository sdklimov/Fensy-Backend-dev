package ru.fensy.dev.usecase.file

import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class StartFileUploadSessionUseCase(
    private val fileUploadSessionRepository: FileUploadSessionRepository,
) : BaseUseCase() {

    suspend fun execute(): UUID {
        val userId = currentUser(required = true)!!.id!!
        return fileUploadSessionRepository.startSession(userId)
    }

}
