package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

data class File (
    val id: UUID? = null,
    val s3Key: String,
    val contextType: FileContextType,
    val contextId: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)