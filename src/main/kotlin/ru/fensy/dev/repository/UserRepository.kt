package ru.fensy.dev.repository

import java.time.OffsetDateTime
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.User
import ru.fensy.dev.domain.UserRole

@Component
class UserRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findById(id: Long): User {
        return databaseClient
            .sql {
                """
                select * from users where id = :id
            """.trimIndent()
            }
            .bind("id", id)
            .fetch()
            .one()
            .map {
                User(
                    id = it["id"] as Long,
                    isVerified = it["is_verified"] as Boolean,
                    fullName = it["full_name"] as? String,
                    username = it["username"] as String,
                    email = it["email"] as? String,
                    avatar = it["avatar"] as? ByteArray,
                    bio = it["bio"] as? String,
                    location = it["location"] as? String,
                    role = UserRole.valueOf(it["role"] as String),
                    website = it["website"] as? String,
                    countryId = it["country_id"] as Long,
                    languageId = it["language_id"] as Long,
                    telegramId = it["telegram_id"] as? String,
                    tonWalletId = it["ton_wallet_id"] as? String,
                    yandexId = it["yandex_id"] as? String,
                    vkId = it["vk_id"] as? String,
                    isActive = it["is_active"] as Boolean,
                    lastLoginAt = it["last_login_at"] as OffsetDateTime,
                    createdAt = it["created_at"] as OffsetDateTime,
                    updatedAt = it["updated_at"] as OffsetDateTime,

                    )
            }
            .awaitSingle()
    }

}