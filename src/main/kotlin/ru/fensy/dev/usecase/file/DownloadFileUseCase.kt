package ru.fensy.dev.usecase.file

import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.ContentDisposition
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import ru.fensy.dev.exception.FileNotFoundException
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class DownloadFileUseCase(
    private val postRepository: PostRepository,
    private val s3FileStorageProxyService: S3FileStorageProxyService,
) : BaseUseCase() {

    suspend fun execute(
        postId: Long,
        fileId: UUID,
        serverWebExchange: ServerWebExchange,
    ) {
        val post = postRepository.findById(postId)
        val user = currentUser(required = true)
        // todo: Тут проверки поста на доступ (все/донатеры и тд..)

        val rs = s3FileStorageProxyService.downloadFile(fileId) ?: throw FileNotFoundException()

        val flux = Flux.from(rs)
            .map { byteBuffer ->
                val readableBytes = byteBuffer.remaining()
                val dataBuffer = DefaultDataBufferFactory().allocateBuffer(readableBytes)
                dataBuffer.write(byteBuffer)
                dataBuffer
            }

        serverWebExchange.response
            .apply {
                headers.apply {
                    contentType = MediaType.APPLICATION_OCTET_STREAM
                    contentDisposition =
                        ContentDisposition
                            .builder("attachment")
                            .filename(rs.response().metadata()["Filename"] as String)
                            .build()
                }
                writeWith(flux)
                    .then()
                    .awaitSingleOrNull()
            }

    }

}
