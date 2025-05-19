//package ru.fensy.dev.service
//
//import org.springframework.stereotype.Service
//import ru.fensy.dev.domain.ParsedLinkType
//import ru.fensy.dev.graphql.controller.post.input.CreatePostInput
//import ru.fensy.dev.properties.PostProperties
//
///**
// * Валидировать запрос на создание поста
// */
//@Service
//class ValidateCreatePostRequestService(
//    private val postProperties: PostProperties,
//) {
//
//    suspend fun validate(input: CreatePostInput): List<String> {
//
//        val validationErrorMessages = mutableListOf<String>()
//
//        input.attachments?.let { attachments ->
//
//            validateLimit(
//                actualCountProvider = { attachments.count { it.headers().contentType.toString() in postProperties.allowedMimeTypes.image } },
//                limit = postProperties.fileTypeAmountLimits.image,
//                errorMessage = WRONG_PICTURE_COUNT_ERROR,
//                validationErrorMessages = validationErrorMessages
//            )
//
//            validateLimit(
//                actualCountProvider = { attachments.count { it.headers().contentType.toString() in postProperties.allowedMimeTypes.video } },
//                limit = postProperties.fileTypeAmountLimits.video,
//                errorMessage = WRONG_VIDEO_COUNT_ERROR,
//                validationErrorMessages = validationErrorMessages
//            )
//
//            validateLimit(
//                actualCountProvider = { attachments.count { it.headers().contentType.toString() in postProperties.allowedMimeTypes.audio } },
//                limit = postProperties.fileTypeAmountLimits.audio,
//                errorMessage = WRONG_AUDIO_COUNT_ERROR,
//                validationErrorMessages = validationErrorMessages
//            )
//        }
//
//        validateLimit(
//            actualCountProvider = { input.parsedLinks?.count { it.type == ParsedLinkType.LINK } ?: 0 },
//            limit = postProperties.fileTypeAmountLimits.link,
//            errorMessage = WRONG_LINKS_COUNT_ERROR,
//            validationErrorMessages = validationErrorMessages
//        )
//
//        validateLimit(
//            actualCountProvider = { input.parsedLinks?.count { it.type == ParsedLinkType.ARTICLE } ?: 0 },
//            limit = postProperties.fileTypeAmountLimits.article,
//            errorMessage = WRONG_ARTICLES_COUNT_ERROR,
//            validationErrorMessages = validationErrorMessages
//        )
//
//        validateLimit(
//            actualCountProvider = { input.parsedLinks?.count { it.type == ParsedLinkType.PODCAST } ?: 0 },
//            limit = postProperties.fileTypeAmountLimits.podcast,
//            errorMessage = WRONG_PODCAST_COUNT_ERROR,
//            validationErrorMessages = validationErrorMessages
//        )
//
//        validateLimit(
//            actualCountProvider = { input.parsedLinks?.count { it.type == ParsedLinkType.PRODUCT } ?: 0 },
//            limit = postProperties.fileTypeAmountLimits.product,
//            errorMessage = WRONG_PRODUCTS_COUNT_ERROR,
//            validationErrorMessages = validationErrorMessages
//        )
//
//        validateLimit(
//            actualCountProvider = { input.collectionIds.size },
//            limit = postProperties.fileTypeAmountLimits.collection,
//            errorMessage = WRONG_COLLECTIONS_COUNT_ERROR,
//            validationErrorMessages = validationErrorMessages
//        )
//
//        return validationErrorMessages
//    }
//
//    fun validateLimit(
//        actualCountProvider: () -> Int,
//        limit: Int,
//        errorMessage: String,
//        validationErrorMessages: MutableList<String>,
//    ) {
//        if (actualCountProvider() > limit) {
//            validationErrorMessages.add(errorMessage)
//        }
//    }
//
//
//    companion object {
//        private const val WRONG_PICTURE_COUNT_ERROR = "В посте не может быть более 10 картинок"
//        private const val WRONG_VIDEO_COUNT_ERROR = "В посте не может быть более 5 видео"
//        private const val WRONG_AUDIO_COUNT_ERROR = "В посте не может быть более 5 аудио"
//        private const val WRONG_LINKS_COUNT_ERROR = "В посте не может быть более 5 ссылок"
//        private const val WRONG_ARTICLES_COUNT_ERROR = "В посте не может быть более 5 статей"
//        private const val WRONG_PODCAST_COUNT_ERROR = "В посте не может быть более 5 подкастов"
//        private const val WRONG_PRODUCTS_COUNT_ERROR = "В посте не может быть более 5 товаров"
//        private const val WRONG_COLLECTIONS_COUNT_ERROR = "В посте не может быть более 5 подборок"
//    }
//
//}
//
