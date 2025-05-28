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

    suspend fun getActiveSessionByIdAndUserId(id: UUID, userId: Long): SessionQueryData? {
        return databaseClient
            .sql(
                """
                select id,  user_id, is_closed, expired_at 
                from file_upload_session where  expired_at > now() and not is_closed
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

    suspend fun closeSession(sessionId: UUID) {
        databaseClient
            .sql(
                """
                update file_upload_session set is_closed = true where id = :sessionId
            """.trimIndent()
            )
            .bind("sessionId", sessionId)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun addFileToSession(sessionId: UUID, fileId: UUID) {
        databaseClient
            .sql(
                """
                insert into file_upload_session_to_file (session_id, file_id) values (:sessionId, :fileId)
            """.trimIndent()
            )
            .bind("sessionId", sessionId)
            .bind("fileId", fileId)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun getSessionFiles(sessionId: UUID): List<UUID> {
        return databaseClient
            .sql(
                """
                select file_id from file_upload_session_to_file
                where session_id = :sessionId
            """.trimIndent()
            )
            .bind("sessionId", sessionId)
            .fetch()
            .all()
            .map { it["file_id"] as UUID }
            .collectList()
            .awaitSingle()
    }

    private fun map(source: Map<String, Any>) = source.let {
        SessionQueryData(
            id = it["id"] as UUID,
            userId = it["user_id"] as Long,
            expiredAt = (it["expired_at"] as OffsetDateTime).withOffsetSameInstant(ZoneOffset.UTC),
            isClosed = it["is_closed"] as Boolean,
        )
    }

    companion object {
        private const val DEFAULT_SESSION_TTL_IN_MINUTES = 60 // todo: вынести в конфиг
    }

}
