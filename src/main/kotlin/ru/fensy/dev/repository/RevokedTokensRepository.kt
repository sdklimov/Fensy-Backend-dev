package ru.fensy.dev.repository

import java.time.OffsetDateTime
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

@Component
class RevokedTokensRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun checkIsRevoked(jti: String, userId: Long): Boolean {
        return databaseClient
            .sql {
                """
                select exists(select 1 from revoked_tokens where token = :jti and user_id = :userId)
            """.trimIndent()
            }
            .bind("jti", jti)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { it["exists"] as Boolean }
            .awaitSingle()

    }

    suspend fun revoke(jti: String, expiredAt: OffsetDateTime, userId: Long) {
        databaseClient
            .sql(
                """
                insert into revoked_tokens(token, expires_at, user_id)
                 values (:jti, :expiredAt, :userId)
            """.trimIndent()
            )
            .bind("jti", jti)
            .bind("expiredAt", expiredAt)
            .bind("userId", userId)
            .fetch()
            .awaitRowsUpdated()
    }

}
