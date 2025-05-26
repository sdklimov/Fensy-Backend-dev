package ru.fensy.dev.proxy

import java.util.UUID
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@Service
class S3FileStorageProxyService(
    private val s3FileStorageProxyClient: WebClient
) {

    fun uploadFile(contentType: String, contentLength: Long, file: Flux<DataBuffer>): UUID {
        return UUID.randomUUID()
    }

}
