package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

data class File (
    val id: UUID? = null,
    val originalFileName: String,
    val storageKey: String,
    val mimeType: String,
    val sizeBytes: Long,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)