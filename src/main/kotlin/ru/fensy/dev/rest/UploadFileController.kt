package ru.fensy.dev.rest

import java.util.UUID
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import ru.fensy.dev.usecase.file.UploadFileUseCase

@RestController
@RequestMapping(path = ["/api/v1/sessions/{sessionId}/files"])
class UploadFileController(
    private val uploadFileUseCase: UploadFileUseCase,
) {

    @PostMapping
    suspend fun uploadFile(
        @PathVariable("sessionId") sessionId: UUID,
        @RequestHeader(HttpHeaders.CONTENT_TYPE, required = true) contentType: String,
        @RequestHeader(HttpHeaders.CONTENT_LENGTH, required = false) contentLength: Long,
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
            file = fileBody
        )
    }

}

data class UploadFileResponse(
    val fileId: UUID
)
