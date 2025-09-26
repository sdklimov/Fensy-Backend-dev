package ru.fensy.dev.proxy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class S3FileStorageProxyService(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Presigner: S3Presigner,
) {

    @Value("\${application.file-storage.s3.bucket-name}")
    private lateinit var bucketName: String

    suspend fun uploadFile(
        contentType: String,
        contentLength: Long,
        file: Flow<DataBuffer>,
        originalFileName: String,
        s3key: String
    ): PutObjectResponse {
        val byteBufferFlow = file.map { dataBuffer ->
            val byteBuffer = dataBuffer.readableByteBuffers().asSequence().first()
            DataBufferUtils.release(dataBuffer)
            byteBuffer
        }

        return uploadFileInternal(
            contentType = contentType,
            contentLength = contentLength,
            originalFileName = originalFileName,
            s3key = s3key,
            asyncRequestBody = AsyncRequestBody.fromPublisher(byteBufferFlow.asPublisher())
        )
    }

    suspend fun uploadFile(
        contentType: String,
        contentLength: Long,
        byteArray: ByteArray,
        originalFileName: String,
        s3key: String
    ): PutObjectResponse {
        return uploadFileInternal(
            contentType = contentType,
            contentLength = contentLength,
            originalFileName = originalFileName,
            s3key = s3key,
            asyncRequestBody = AsyncRequestBody.fromBytes(byteArray)
        )
    }

    private suspend fun uploadFileInternal(
        contentType: String,
        contentLength: Long,
        originalFileName: String,
        s3key: String,
        asyncRequestBody: AsyncRequestBody
    ): PutObjectResponse {

        return s3AsyncClient.putObject(
            PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3key)
                .contentLength(contentLength)
                .metadata(mapOf("fileName" to originalFileName))
                .contentType(contentType)
                .build(),
            asyncRequestBody
        ).await()
    }

    suspend fun generatePresignedUrl(
        s3key: String,
        expirationDuration: Duration = Duration.ofHours(1) /*TODO вынести в env*/
    ): String {
        val headObjectResponse = s3AsyncClient.headObject(
            HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(s3key)
                .build()
        ).await()

        val fileName = headObjectResponse.metadata()["filename"] ?: "download"

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
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

    suspend fun downloadFileAsStream(s3key: String): Flow<DataBuffer> {
        val publisher = s3AsyncClient.getObject(
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3key)
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).await()

        return publisher.asFlow().map { byteBuffer ->
            DefaultDataBufferFactory().wrap(byteBuffer)
        }
    }

    /**
     * Удаляет файл из S3 хранилища
     */
    suspend fun deleteFile(s3key: String): DeleteObjectResponse {
        return s3AsyncClient.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3key)
                .build()
        ).await()
    }
}
