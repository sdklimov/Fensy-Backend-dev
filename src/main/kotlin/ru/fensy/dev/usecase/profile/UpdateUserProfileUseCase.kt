package ru.fensy.dev.usecase.profile

import org.springframework.stereotype.Component
import ru.fensy.dev.exception.UserAlreadyExistsException
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.graphql.controller.userprofile.input.UserProfileInput
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
class UpdateUserProfileUseCase(
    private val userRepository: UserRepository,
) : BaseUseCase() {

    suspend fun execute(userProfile: UserProfileInput): UserResponse {
        val user = currentUser(true)!!

        if (user.username != userProfile.username) {
            val exists = userRepository.checkUserExistsByUsername(userProfile.username)

            if (exists) {
                throw UserAlreadyExistsException("Пользователь ${user.username} уже существует")
            }
        }

        val updated = userRepository.update(
            user.copy(
                fullName = userProfile.fullName,
                username = userProfile.username,
                email = userProfile.email,
                bio = userProfile.bio,
                location = userProfile.location,
                website = userProfile.website,
                countryId = userProfile.countryId,
                languageId = userProfile.languageId,
            )
        )

        return UserResponse(user = updated, message = "Пользователь обновлен", success = true)

    }


}

