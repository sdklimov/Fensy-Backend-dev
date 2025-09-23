package ru.fensy.dev.repository

import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.RefreshToken
import ru.fensy.dev.graphql.controller.auth.response.RefreshResponse

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

    suspend fun getRefreshToken(tokenHash: String): RefreshToken? {
        return databaseClient
            .sql(
                """
                select id, user_id, token_hash, jwt_id, expires_at, revoked
                from refresh_tokens
                where token_hash = :tokenHash 
                and expires_at > now()
                and revoked = false
            """.trimIndent()
            )
            .bind("tokenHash", tokenHash)
            .fetch()
            .one()
            .map {
                RefreshToken(
                    id = it["id"] as UUID,
                    userId = it["user_id"] as Long,
                    tokenHash = it["token_hash"] as String,
                    jwtId = it["jwt_id"] as String,
                    expiresAt = it["expires_at"] as OffsetDateTime,
                    revoked = it["revoked"] as Boolean
                )
            }
            .awaitSingleOrNull()
    }

    suspend fun revokeToken(id: UUID) {
        databaseClient
            .sql(
                """
                update refresh_tokens set revoked = true where id = :id
            """.trimIndent()
            )
            .bind("id", id)
            .fetch()
            .awaitRowsUpdated()
    }

}
