package ru.fensy.dev.proxy

import kotlinx.coroutines.future.await
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ru.fensy.dev.configuration.s3.S3ClientConfigurationProperties
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.core.async.ResponsePublisher
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class S3FileStorageProxyService(
    private val s3Client: S3AsyncClient,
    private val s3Presigner: S3Presigner,
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
            val byteBuffer = dataBuffer.readableByteBuffers().asSequence().first()
            DataBufferUtils.release(dataBuffer)
            byteBuffer
        }

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

    suspend fun generatePresignedUrl(s3key: String, expirationDuration: Duration = Duration.ofHours(1) /*TODO вынести в env*/): String {
        // Получаем metadata для извлечения fileName
        val headObjectResponse = s3Client.headObject(
            HeadObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(s3key)
                .build()
        ).await()

        val fileName = headObjectResponse.metadata()["filename"] ?: "download"

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(properties.bucketName)
            .key(s3key)
            .responseContentDisposition("attachment; filename=\"$fileName\"")
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expirationDuration)
            .getObjectRequest(getObjectRequest)
            .build()

        val presignedRequest = s3Presigner.presignGetObject(presignRequest)

        return presignedRequest.url().toString()
    }

    suspend fun downloadFile(s3key: String): ResponsePublisher<GetObjectResponse>? {
        // Получаем metadata для извлечения fileName
        val headObjectResponse = s3Client.headObject(
            HeadObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(s3key)
                .build()
        ).await()

        val fileName = headObjectResponse.metadata()["filename"] ?: "download"

        return s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(properties.bucketName)
                .key(s3key)
                .responseContentDisposition("attachment; filename=\"$fileName\"")
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).await()
    }
}
