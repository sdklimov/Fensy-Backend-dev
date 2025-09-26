package ru.fensy.dev.extensions

import ru.fensy.dev.domain.MediaAssetType
import java.security.MessageDigest

fun String.sha256(): String {
    val bytes = this.toByteArray(Charsets.UTF_8)
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun String.toLikeQuery(): String = "%$this%"

fun String.determineAssetType(): MediaAssetType {
    return when {
        this.startsWith("image/") -> MediaAssetType.IMAGE
        this.startsWith("video/") -> MediaAssetType.VIDEO
        this.startsWith("audio/") -> MediaAssetType.AUDIO
        this in listOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        ) -> MediaAssetType.DOCUMENT
        else -> MediaAssetType.OTHER
    }
}