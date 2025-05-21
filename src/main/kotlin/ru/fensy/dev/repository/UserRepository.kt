package ru.fensy.dev.repository

import java.time.OffsetDateTime
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
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
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun findByUsername(username: String): User? {
        return databaseClient
            .sql(
                """
                select * from users where username = :username and is_active
            """.trimIndent()
            )
            .bind("username", username)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun create(user: User): User {
        return databaseClient
            .sql(
                """
                insert into users(full_name, username, email, avatar, bio, location, role, website, telegram_id, ton_wallet_id, yandex_id, vk_id, is_active, country_id, language_id) 
                values (:fullName, :username, :email, :avatar, :bio, :location, :role, :website, :telegramId, :tonWalletId, :yandexId, :vkId, :isActive, :countryId, :languageId)
                on conflict (username) do nothing 
                returning *
            """.trimIndent()
            )
            .bind("fullName", user.fullName)
            .bind("username", user.username)
            .bind("email", user.email)
            .bind("avatar", user.avatar)
            .bind("bio", user.bio)
            .bind("location", user.location)
            .bind("role", user.role)
            .bind("website", user.website)
            .bind("telegramId", user.telegramId)
            .bind("tonWalletId", user.tonWalletId)
            .bind("yandexId", user.yandexId)
            .bind("vkId", user.vkId)
            .bind("isActive", user.isActive)
            .bind("countryId", user.countryId)
            .bind("languageId", user.languageId)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>) =
        source.let {
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

}