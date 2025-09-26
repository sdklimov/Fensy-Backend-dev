package ru.fensy.dev.graphql.responses

import java.util.UUID

data class PostAttachment(
    val postId: Long,
    val video: List<VideoPostAttachment>?,
    val image: List<ImagePostAttachment>?,
)

data class VideoPostAttachment(
    val assetId: UUID,
    val playback: VideoAsset?
)

data class ImagePostAttachment(
    val assetId: UUID,
    val thumbnail: ImageAsset?,
    val medium: ImageAsset?,
    val large: ImageAsset?,
)

