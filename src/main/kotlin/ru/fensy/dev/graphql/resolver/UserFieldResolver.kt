package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.*
import ru.fensy.dev.graphql.responses.AvatarVariants
import ru.fensy.dev.graphql.responses.ImageAsset
import ru.fensy.dev.proxy.S3FileStorageProxyService
import ru.fensy.dev.repository.*

@FieldResolver
class UserFieldResolver(
    private val postRepository: PostRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val countryRepository: CountriesRepository,
    private val languagesRepository: LanguagesRepository,
    private val mediaFileRepository: MediaFileRepository,
    private val s3FileStorageProxyService: S3FileStorageProxyService
) {

    @SchemaMapping(typeName = "User", field = "posts")
    suspend fun posts(user: User): List<Post> {
        return postRepository.findByAuthorId(user.id!!)
    }

    @SchemaMapping(typeName = "User", field = "avatar")
    suspend fun avatar(user: User): AvatarVariants? {
        return user.avatar?.let { avatarAssetId ->
            AvatarVariants(avatarAssetId = avatarAssetId)
        }
    }

    @SchemaMapping(typeName = "AvatarVariants", field = "thumbnail")
    suspend fun avatarThumbnail(avatarVariants: AvatarVariants): ImageAsset? {
        val avatarAssetId = avatarVariants.avatarAssetId ?: return null

        val thumbnailFile = mediaFileRepository.findByMediaAssetIdAndCompressionSize(
            avatarAssetId, MediaFileCompressionSize.THUMBNAIL
        ) ?: mediaFileRepository.findOriginalByMediaAssetId(avatarAssetId)

        return thumbnailFile?.let { file ->
            ImageAsset(
                url = s3FileStorageProxyService.generatePresignedUrl(file.storageKey),
                width = file.width,
                height = file.height,
                mimeType = file.mimeType,
                originalFilename = file.originalFileName
            )
        }
    }

    @SchemaMapping(typeName = "AvatarVariants", field = "medium")
    suspend fun avatarMedium(avatarVariants: AvatarVariants): ImageAsset? {
        val avatarAssetId = avatarVariants.avatarAssetId ?: return null

        val mediumFile = mediaFileRepository.findByMediaAssetIdAndCompressionSize(
            avatarAssetId, MediaFileCompressionSize.MEDIUM
        ) ?: mediaFileRepository.findOriginalByMediaAssetId(avatarAssetId)

        return mediumFile?.let { file ->
            ImageAsset(
                url = s3FileStorageProxyService.generatePresignedUrl(file.storageKey),
                width = file.width,
                height = file.height,
                mimeType = file.mimeType,
                originalFilename = file.originalFileName
            )
        }
    }

    @SchemaMapping(typeName = "AvatarVariants", field = "large")
    suspend fun avatarLarge(avatarVariants: AvatarVariants): ImageAsset? {
        val avatarAssetId = avatarVariants.avatarAssetId ?: return null

        val largeFile = mediaFileRepository.findByMediaAssetIdAndCompressionSize(
            avatarAssetId, MediaFileCompressionSize.LARGE
        ) ?: mediaFileRepository.findOriginalByMediaAssetId(avatarAssetId)

        return largeFile?.let { file ->
            ImageAsset(
                url = s3FileStorageProxyService.generatePresignedUrl(file.storageKey),
                width = file.width,
                height = file.height,
                mimeType = file.mimeType,
                originalFilename = file.originalFileName
            )
        }
    }

    @SchemaMapping(typeName = "User", field = "settings")
    suspend fun settings(user: User): UserSettings {
        return userSettingsRepository.getByUserId(user.id!!)
    }

    @SchemaMapping(typeName = "User", field = "country")
    suspend fun country(user: User): Country {
        return countryRepository.getById(user.countryId)
    }

    @SchemaMapping(typeName = "User", field = "language")
    suspend fun language(user: User): Language {
        return languagesRepository.getById(user.languageId)
    }

}
