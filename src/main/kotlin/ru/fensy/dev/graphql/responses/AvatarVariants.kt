package ru.fensy.dev.graphql.responses

import java.util.*


data class AvatarVariants(
    val avatarAssetId: UUID? = null,
    val thumbnail: ImageAsset? = null,
    val medium: ImageAsset? = null,
    val large: ImageAsset? = null
)

