package ru.fensy.dev.rest.sessions

import org.springframework.web.bind.annotation.*
import ru.fensy.dev.usecase.file.UploadFileToSessionUseCase
import java.util.*

@RestController
@RequestMapping(path = ["/api/v1/sessions/{sessionId}/files/{fileId}"])
class RemoveFileFromSessionController(
    private val uploadFileToSessionUseCase: UploadFileToSessionUseCase,
) {

    @DeleteMapping
    suspend fun uploadFile(
        @PathVariable("sessionId") sessionId: UUID,
        @PathVariable("fileId") fileId: UUID
    ): UploadFileResponse {
        return UploadFileResponse(fileId)
//        return uploadFileToSessionUseCase.execute(
//            sessionId = sessionId,
//            contentType = contentType,
//            contentLength = contentLength,
//            file = fileBody,
//            fileName = fileName,
//        )
    }

}