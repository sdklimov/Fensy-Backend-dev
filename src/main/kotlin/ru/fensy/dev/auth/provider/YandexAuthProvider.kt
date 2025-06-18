package ru.fensy.dev.auth.provider

import java.time.OffsetDateTime
import java.util.Base64
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import ru.fensy.dev.auth.domain.AuthResult
import ru.fensy.dev.domain.User
import ru.fensy.dev.domain.UserRole
import ru.fensy.dev.repository.CountriesRepository
import ru.fensy.dev.repository.LanguagesRepository
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.repository.UserSettingsRepository
import ru.fensy.dev.service.YandexUserInfoProxyService
import ru.fensy.dev.service.avatar.DefaultAvatarService

@Component
class YandexAuthProvider(
    private val yandexUserInfoProxyService: YandexUserInfoProxyService,
    private val userRepository: UserRepository,
    private val countriesRepository: CountriesRepository, //todo: Вынести в сервис с ленивой загрузкой (кеш)
    private val languagesRepository: LanguagesRepository, //todo: Вынести в сервис с ленивой загрузкой (кеш)
    private val defaultAvatarService: DefaultAvatarService,
    private val userSettingsRepository: UserSettingsRepository,
) : AuthProvider {

    override fun name(): String = PROVIDER_NAME

    override suspend fun auth(accessToken: String): AuthResult {
        val userInfo = yandexUserInfoProxyService
            .getUserInfo(accessToken)
        val createUserResult = getOrCreateUser(userInfo)

        return AuthResult(isUserCreated = createUserResult.isCreated, user = createUserResult.user)
    }

    private suspend fun getOrCreateUser(userInfo: Map<String, Any>): CreateUserOperationResult {
        val yandexId = userInfo["id"] as String

        return userRepository.findByYandexId(yandexId)?.let {
            CreateUserOperationResult(isCreated = false, user = it)
        } ?: create(userInfo)
    }

    private suspend fun create(userInfo: Map<String, Any>): CreateUserOperationResult = coroutineScope {

        val countryId = async {countriesRepository.getByCode("ru").id }
        val langId = async {  languagesRepository.getByCode("ru").id }

        val avatar = Base64.getEncoder().encodeToString(defaultAvatarService.getRandomAvatar())

        val user = User(
            isVerified = false,
            fullName = userInfo["real_name"] as? String ?: (userInfo["display_name"] as? String),
            username = userInfo["login"] as String,
            email = (userInfo["emails"] as List<String>).first(),
            avatar = avatar,
            bio = null,
            location = null,
            role = UserRole.USER,
            website = null,
            countryId = countryId.await(),
            languageId = langId.await(),
            telegramId = null,
            tonWalletId = null,
            yandexId = userInfo["id"] as String,
            vkId = null,
            isActive = true,
            lastLoginAt = OffsetDateTime.now(),
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
        val created = userRepository.create(user)
        userSettingsRepository.create(created)
        return@coroutineScope CreateUserOperationResult(isCreated = true, user = created)
    }


    companion object {
        private const val PROVIDER_NAME = "Yandex"
    }
}


data class CreateUserOperationResult(
    val isCreated: Boolean, val user: User,
)