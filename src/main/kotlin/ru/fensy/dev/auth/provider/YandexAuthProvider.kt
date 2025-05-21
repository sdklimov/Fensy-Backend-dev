package ru.fensy.dev.auth.provider

import java.time.OffsetDateTime
import org.springframework.stereotype.Component
import ru.fensy.dev.auth.domain.AuthResult
import ru.fensy.dev.domain.User
import ru.fensy.dev.domain.UserRole
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.service.YandexUserInfoProxyService

@Component
class YandexAuthProvider(
    private val yandexUserInfoProxyService: YandexUserInfoProxyService,
    private val userRepository: UserRepository,
) : AuthProvider {

    override fun name(): String = PROVIDER_NAME

    override suspend fun auth(accessToken: String): AuthResult {
        val userInfo = yandexUserInfoProxyService
            .getUserInfo(accessToken)
        val createUserResult = getOrCreateUser(userInfo)

        return AuthResult(isUserCreated = createUserResult.isCreated, bearerToken = "")
    }

    private suspend fun getOrCreateUser(userInfo: Map<String, Any>): CreateUserOperationResult {
        val login = userInfo["login"] as String

        return userRepository.findByUsername(login)?.let {
            CreateUserOperationResult(isCreated = false, user = it)
        } ?: create(userInfo)
    }

    private suspend fun create(userInfo: Map<String, Any>): CreateUserOperationResult {

        val user = User(
            id = 0,
            isVerified = false,
            fullName = null,
            username = "",
            email = null,
            avatar = null,
            bio = null,
            location = null,
            role = UserRole.USER,
            website = null,
            countryId = 0,
            languageId = 0,
            telegramId = null,
            tonWalletId = null,
            yandexId = null,
            vkId = null,
            isActive = false,
            lastLoginAt = OffsetDateTime.now(),
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
        val created = userRepository.create(user)
        return CreateUserOperationResult(isCreated = true, user = created)
    }


    companion object {
        private const val PROVIDER_NAME = "Yandex"
    }
}


data class CreateUserOperationResult(
    val isCreated: Boolean, val user: User,
)