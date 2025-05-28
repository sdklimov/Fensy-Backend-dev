package ru.fensy.dev.proxy

import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.fensy.dev.configuration.s3.S3ClientConfigurationProperties
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class S3FileStorageProxyService(
    private val s3Client: S3AsyncClient,
    private val properties: S3ClientConfigurationProperties,
) {

    suspend fun uploadFile(contentType: String, contentLength: Long, file: Flux<DataBuffer>): UUID {
        val byteBufferFlux = file.transform { flux ->
            flux.map { dataBuffer ->
                try {
                    dataBuffer.asByteBuffer()
                } finally {
                    DataBufferUtils.release(dataBuffer) // освобождаем ресурсы
                }
            }
        }

        val fileKey = UUID.randomUUID()

        Mono.fromFuture(
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(properties.bucketName)
                    .key(fileKey.toString())
                    .contentLength(contentLength)
                    .contentType(contentType)
                    .build(),
                AsyncRequestBody.fromPublisher(byteBufferFlux)
            )
        ).awaitSingle()

        return fileKey
    }

}

