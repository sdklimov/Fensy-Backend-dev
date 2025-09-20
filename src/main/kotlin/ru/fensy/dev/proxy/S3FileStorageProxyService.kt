package ru.fensy.dev.proxy

import kotlinx.coroutines.future.await
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.PooledDataBuffer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.fensy.dev.configuration.s3.S3ClientConfigurationProperties
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.core.async.ResponsePublisher
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

@Service
class S3FileStorageProxyService(
    private val s3Client: S3AsyncClient,
    private val properties: S3ClientConfigurationProperties,
) {

    suspend fun uploadFile(
        contentType: String,
        contentLength: Long,
        file: Flux<DataBuffer>,
        originalFileName: String,
        s3key: String
    ): PutObjectResponse {
        val byteBufferFlux = file.map { dataBuffer ->
            dataBuffer.asByteBuffer()
        }.doOnDiscard(PooledDataBuffer::class.java, DataBufferUtils::release)

        return s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(s3key)
                .metadata(mapOf("fileName" to originalFileName))
                .contentLength(contentLength)
                .contentType(contentType)
                .build(),
            AsyncRequestBody.fromPublisher(byteBufferFlux)
        ).await()
    }

    suspend fun downloadFile(s3key: String): ResponsePublisher<GetObjectResponse>? {
        return s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(s3key)
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).await()
    }
}
