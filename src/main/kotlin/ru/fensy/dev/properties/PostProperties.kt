package ru.fensy.dev.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.post-properties")
data class PostProperties(
    val allowedMimeTypes: AllowedMimeTypesProperties,
    val fileTypeAmountLimits: FileTypeAmountLimitsProperties
) {
    val allAllowedMimeTypes = (allowedMimeTypes.audio + allowedMimeTypes.video + allowedMimeTypes.image).toHashSet()
}

data class AllowedMimeTypesProperties(
    val image: HashSet<String>,
    val video: HashSet<String>,
    val audio: HashSet<String>,
)

data class FileTypeAmountLimitsProperties(
    val image: Int,
    val video: Int,
    val audio: Int,
    val link: Int,
    val article: Int,
    val podcast: Int,
    val product: Int,
    val collection: Int,
)
