package ru.fensy.dev.configuration.s3

import java.net.URI
import java.time.Duration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(S3ClientConfigurationProperties::class)
class S3ClientConfiguration(
    private val properties: S3ClientConfigurationProperties,
) {

    @Bean
    fun s3Client(): S3AsyncClient {
        return S3AsyncClient.builder()
            .httpClient(
                NettyNioAsyncHttpClient.builder()
                    .writeTimeout(Duration.ZERO)
                    .maxConcurrency(64)
                    .build()
            )
            .region(Region.of(properties.region))
            .endpointOverride(URI.create(properties.endpoint))
            .forcePathStyle(true)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        properties.keyId,
                        properties.keySecret
                    )
                )
            )
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val s3Configuration = S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
        return S3Presigner.builder()
            .serviceConfiguration(s3Configuration)
            .region(Region.of(properties.region))
            .endpointOverride(URI.create(properties.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        properties.keyId,
                        properties.keySecret
                    )
                )
            )
            .build()
    }
}
