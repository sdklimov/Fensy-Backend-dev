package ru.fensy.dev.proxy

import java.net.URI
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class S3FileStorageProxyService(
    private val s3FileStorageProxyClient: WebClient,
) {

    companion object {
        private const val KEY_ID = "YCAJE6_MXBEkMqZc_OU3OVwU_"
        private const val SECRET = "YCNMQf-q0e9eWP2KSpE2b_aJiJTx-aXCerPe03nB"
        private const val REGION = "ru-central1"
        private const val S3_ENDPOINT = "https://storage.yandexcloud.net"
        private const val BUCKET = "fansy-storage"


        private const val MEDIA_TYPE = "image/jpeg"
    }


    suspend fun uploadFile(contentType: String, contentLength: Long, file: Flux<DataBuffer>): UUID {
        val credentials = AwsBasicCredentials.create(KEY_ID, SECRET)

        val byteBufferFlux = file.transform { flux ->
            flux.map { dataBuffer ->
                try {
//                    dataBuffer.readableByteBuffers()
                    dataBuffer.asByteBuffer()
                } finally {
                    DataBufferUtils.release(dataBuffer) // освобождаем ресурсы
                }
            }
        }

        val fileKey = UUID.randomUUID()

        val s3Client = S3AsyncClient.builder()
            .httpClient(
                NettyNioAsyncHttpClient.builder()
                    .writeTimeout(Duration.ZERO)
                    .maxConcurrency(64)
                    .build()
            )
            .region(Region.of(REGION))
            .endpointOverride(URI.create(S3_ENDPOINT))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()

        Mono.fromFuture(
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(fileKey.toString())
                    .contentLength(contentLength)
                    .contentType(MEDIA_TYPE)
                    .build(),
                AsyncRequestBody.fromPublisher(byteBufferFlux)
            )
        ).awaitSingle()

        return fileKey
    }

}

