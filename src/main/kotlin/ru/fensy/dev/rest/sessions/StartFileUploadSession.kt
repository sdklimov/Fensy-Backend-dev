package ru.fensy.dev.rest.sessions

import java.util.UUID
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.usecase.sessions.StartFileUploadSessionUseCase

@RestController
@RequestMapping(path = ["/api/v1/files/sessions"])
class StartFileUploadSession(
    private val startFileUploadSessionUseCase: StartFileUploadSessionUseCase,
) {

    @PostMapping
    suspend fun uploadFile(): SessionIdResponse = SessionIdResponse(startFileUploadSessionUseCase.execute())
}

data class SessionIdResponse(val sessionId: UUID)