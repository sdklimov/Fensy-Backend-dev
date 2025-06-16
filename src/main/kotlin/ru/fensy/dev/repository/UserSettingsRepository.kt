package ru.fensy.dev.repository

import java.time.OffsetDateTime
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.AllowMessagesFrom
import ru.fensy.dev.domain.User
import ru.fensy.dev.domain.UserSettings

@Component
class UserSettingsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun create(user: User) {
        databaseClient
            .sql("""
                insert into user_settings(user_id, allow_messages_from, notifications_on_email, ad_on_email)
                values (:userId, 'ANY', true, true )
            """.trimIndent())
            .bind("userId", user.id!!)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun getByUserId(userId: Long): UserSettings {
        return databaseClient
            .sql(
                """
                select * from user_settings where user_id = :userId
            """.trimIndent()
            )
            .bind("userId", userId)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>) = source.let {
        UserSettings(
            id = it["id"] as Long,
            userId = it["user_id"] as Long,
            allowMessagesFrom = AllowMessagesFrom.valueOf(it["allow_messages_from"] as String),
            notificationsOnEmail = it["notifications_on_email"] as Boolean,
            adOnEmail = it["ad_on_email"] as Boolean,
            createdAt = it["created_at"] as OffsetDateTime,
            updatedAt = it["updated_at"] as OffsetDateTime,
        )
    }

}
