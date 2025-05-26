package ru.fensy.dev.querydata.session

import java.time.OffsetDateTime
import java.util.UUID

data class SessionQueryData(
    val id: UUID,
    val userId: Long,
    val expiredAt: OffsetDateTime,
)