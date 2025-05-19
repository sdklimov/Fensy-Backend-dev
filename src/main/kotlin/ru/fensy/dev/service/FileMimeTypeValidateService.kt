package ru.fensy.dev.service

import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import ru.fensy.dev.properties.PostProperties

/**
 * Сервис валидации mime-type файлов
 */
@Service
class FileMimeTypeValidateService(
    private val postProperties: PostProperties,
) {

    suspend fun validate(source: List<FilePart>): ValidateFileMimeTypeValidationResult {
        source.forEach {
            if (!postProperties.allAllowedMimeTypes.contains(it.headers().contentType.toString())) {
                return ValidateFileMimeTypeValidationResult(isSuccessful = false, message = WRONG_FILE_FORMAT_MESSAGE)
            }
        }
        return ValidateFileMimeTypeValidationResult(isSuccessful = true)
    }

    companion object {
        private const val WRONG_FILE_FORMAT_MESSAGE =
            "Недопустимый формат вложения. Разрешены только следующие форматы: изображения (JPEG, PNG, GIF, WebP), \\\n" +
                "видео (MP4, WebM, OGG), аудио (MPEG, OGG, WAV)."
    }

    data class ValidateFileMimeTypeValidationResult(
        val isSuccessful: Boolean,
        val message: String? = null,
    ) {
        fun isNotSuccessful() = !isSuccessful
    }

}
