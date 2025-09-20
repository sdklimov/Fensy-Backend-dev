package ru.fensy.dev.usecase.user

import org.springframework.stereotype.Component
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
class SetInterestsToUserUseCase(private val interestsRepository: InterestsRepository) : BaseUseCase() {

    suspend fun execute(interestIds: List<Long>): UserResponse {
        val user = currentUser(required = true)!!
        interestsRepository.addUserInterests(user.id!!, interestIds)
        return UserResponse(user = user, success = true, message = "Интересы успешно добавлены")
    }
}