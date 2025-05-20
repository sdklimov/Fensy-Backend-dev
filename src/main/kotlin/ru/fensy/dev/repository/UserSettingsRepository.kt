package ru.fensy.dev.repository

import java.time.OffsetDateTime
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.AllowMessagesFrom
import ru.fensy.dev.domain.UserSettings

@Component
class UserSettingsRepository(
    private val databaseClient: DatabaseClient,
) {

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
