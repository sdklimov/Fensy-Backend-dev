package ru.fensy.dev.repository

import java.time.Duration
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

/**
 * Репозиторий refresh_token
 */
@Component
class RefreshTokenRepository(
    private val databaseClient: DatabaseClient,
) {

    /**
     * Сохранить токен
     */
    suspend fun save(userId: Long, tokenHash: String, jwtId: String, expiresAt: Duration) {
        databaseClient.sql(
            """
            insert into refresh_tokens(user_id, token_hash, jwt_id, expires_at)
            values (:userId, :tokenHash, :jwtId, now() + interval '${expiresAt.toMinutes()} minutes')
        """.trimIndent()
        )
            .bind("userId", userId)
            .bind("tokenHash", tokenHash)
            .bind("jwtId", jwtId)
            .fetch()
            .awaitRowsUpdated()
    }

}
