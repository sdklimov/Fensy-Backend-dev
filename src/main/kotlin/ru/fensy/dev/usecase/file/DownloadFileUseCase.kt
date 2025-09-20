package ru.fensy.dev.usecase.file

import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.exception.FileNotFoundException
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.FileRepository
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class DownloadFileUseCase(
    private val postRepository: PostRepository,
    private val s3FileStorageProxyService: S3FileStorageProxyService,
    private val fileRepository: FileRepository,
) : BaseUseCase() {

    suspend fun execute(
        fileId: UUID,
        serverWebExchange: ServerWebExchange,
    ) {
        val file = fileRepository.findById(fileId) ?: throw FileNotFoundException()
        val rs = s3FileStorageProxyService.downloadFile(file.s3Key) ?: throw FileNotFoundException()

        val dataBufferFlux = rs.map { byteBuffer ->
            DefaultDataBufferFactory().wrap(byteBuffer)
        }

        serverWebExchange.response
            .apply {
                headers.apply {
                    contentType = MediaType.APPLICATION_OCTET_STREAM
                    contentDisposition =
                        ContentDisposition
                            .builder("attachment")
                            .filename(rs.response().metadata()["filename"] as String)
                            .build()
                }
                writeWith(dataBufferFlux)
                    .then()
                    .awaitSingleOrNull()
            }

    }

}
