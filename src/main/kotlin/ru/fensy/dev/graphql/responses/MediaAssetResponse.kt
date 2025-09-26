package ru.fensy.dev.graphql.responses

data class ImageAsset(
    val url: String,
    val width: Int?,
    val height: Int?,
    val mimeType: String?,
    val originalFilename: String?
)

data class VideoAsset(
    val url: String,
    val width: Int?,
    val height: Int?,
    val mimeType: String?,
    val originalFilename: String?,
    val durationSeconds: Int?
)