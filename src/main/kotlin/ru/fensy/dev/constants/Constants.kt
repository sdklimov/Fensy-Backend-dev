package ru.fensy.dev.constants

import ru.fensy.dev.exception.ContentModerationException

object Constants {

    const val CURRENT_USER_CONTEXT_KEY = "currentUser"
    const val REQUEST_HTTP_HEADERS = "httpHeaders"
    const val JWT_CLAIMS = "claims"
    const val JTI_CLAIM_NAME = "jti"

    const val X_FILE_CONTENT_TYPE_HEADER_NAME = "x-content-type"
    const val X_FILE_CONTENT_LENGTH_HEADER_NAME = "x-content-length"
    const val X_FILE_NAME_HEADER_NAME = "x-file-name"

    const val DEFAULT_VIDEO_FORMAT = "mp4"
    const val DEFAULT_VIDEO_PREVIEW_FORMAT = "mjpeg"
    const val DEFAULT_IMAGE_MIMETYPE = "mjpeg"
    const val DEFAULT_IMAGE_FORMAT = "jpg"

    val CONTENT_MODERATION_EXCEPTION =
        ContentModerationException("Ваше сообщение не прошло модерацию. Пожалуйста, убедитесь, что оно не содержит оскорблений, мата или агрессивного содержания.")
}