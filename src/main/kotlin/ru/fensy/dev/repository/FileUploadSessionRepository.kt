package ru.fensy.dev.repository

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.querydata.session.SessionQueryData

@Component
class FileUploadSessionRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun startSession(userId: Long): UUID =
        databaseClient
            .sql(
                """insert into file_upload_session (expired_at, user_id) values 
             |  (now() + interval '$DEFAULT_SESSION_TTL_IN_MINUTES minutes', :userId) returning id
             |  """.trimMargin()
            )
            .bind("userId", userId)
            .fetch()
            .one()
            .map { it["id"] as UUID }
            .awaitSingle()

    suspend fun getByIdAndUserId(id: UUID, userId: Long): SessionQueryData? {
        return databaseClient
            .sql(
                """
                select id,  user_id, expired_at from file_upload_session where  expired_at > now()
                and id = :id and user_id = :userId
            """.trimIndent()
            )
            .bind("id", id)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { map(it) }
            .awaitSingleOrNull()
    }

    suspend fun addFileToSession(sessionId: UUID, fileId: UUID) {
        databaseClient
            .sql("""
                insert into file_upload_session_to_file (session_id, file_id) values (:sessionId, :fileId)
            """.trimIndent())
            .bind("sessionId", sessionId)
            .bind("fileId", fileId)
            .fetch()
            .awaitRowsUpdated()
    }

    private fun map(source: Map<String, Any>) = source.let {
        SessionQueryData(
            id = it["id"] as UUID,
            userId = it["user_id"] as Long,
            expiredAt = (it["expired_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
        )
    }

    companion object {
        private const val DEFAULT_SESSION_TTL_IN_MINUTES = 60 // todo: вынести в конфиг
    }

}
