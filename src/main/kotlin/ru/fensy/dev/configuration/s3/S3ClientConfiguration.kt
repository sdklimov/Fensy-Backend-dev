package ru.fensy.dev.configuration.s3

import io.awspring.cloud.autoconfigure.core.AwsProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class S3ClientConfiguration {

    @Bean
    fun s3AsyncClient(
        awsCredentialsProvider: AwsCredentialsProvider,
        @Value("\${spring.cloud.aws.region.static}") region: String,
        @Value("\${spring.cloud.aws.s3.endpoint}") endpoint: URI,
    ): S3AsyncClient {
        return S3AsyncClient.builder()
            .httpClient(
                NettyNioAsyncHttpClient.builder()
                    .writeTimeout(Duration.ZERO)
                    .maxConcurrency(64)
                    .build()
            )
            .region(Region.of(region))
            .endpointOverride(endpoint)
            .credentialsProvider(awsCredentialsProvider)
            .build()
    }
}
