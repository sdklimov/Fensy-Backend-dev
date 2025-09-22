package ru.fensy.dev.rest

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.constants.Constants.X_FILE_CONTENT_LENGTH_HEADER_NAME
import ru.fensy.dev.constants.Constants.X_FILE_CONTENT_TYPE_HEADER_NAME
import ru.fensy.dev.constants.Constants.X_FILE_NAME_HEADER_NAME
import ru.fensy.dev.usecase.file.UploadFileUseCase
import java.util.*

@RestController
@RequestMapping(path = ["/api/v1/sessions/{sessionId}/files"])
class UploadFileController(
    private val uploadFileUseCase: UploadFileUseCase,
) {

    @PostMapping(consumes = ["multipart/form-data"])
    @RequestBody(
        content = [Content(
            mediaType = "multipart/form-data",
        )]
    )
    suspend fun uploadFile(
        @PathVariable("sessionId") sessionId: UUID,
        @RequestHeader(X_FILE_CONTENT_TYPE_HEADER_NAME, required = true) contentType: String,
        @RequestHeader(X_FILE_CONTENT_LENGTH_HEADER_NAME, required = true) contentLength: Long,
        @RequestHeader(X_FILE_NAME_HEADER_NAME, required = true) fileName: String,
        serverWebExchange: ServerWebExchange,
    ): UploadFileResponse {
        val fileBody = serverWebExchange
            .multipartData
            .flatMapMany {
                (it["file"]!!.first() as FilePart).content()
            }


        return uploadFileUseCase.execute(
            sessionId = sessionId,
            contentType = contentType,
            contentLength = contentLength,
            file = fileBody,
            fileName = fileName,
        )
    }

}

data class UploadFileResponse(
    val fileId: UUID
)
