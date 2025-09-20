package ru.fensy.dev.rest

import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.usecase.file.DownloadFileUseCase

@RestController
@RequestMapping(path = ["/api/v1"])
class DownloadFileController(
    private val downloadFileUseCase: DownloadFileUseCase,
) {

    @GetMapping("/files/{fileId}")
    suspend fun downloadFile(
        @PathVariable("fileId") fileId: UUID,
        serverWebExchange: ServerWebExchange,
    ) {
        downloadFileUseCase.execute(fileId, serverWebExchange)
    }

}
