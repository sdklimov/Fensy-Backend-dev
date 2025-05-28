package ru.fensy.dev.configuration.s3

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.file-storage.s3")
data class S3ClientConfigurationProperties(
    val bucketName: String,
    val endpoint: String,
    val keyId: String,
    val keySecret: String,
    val region: String
)
