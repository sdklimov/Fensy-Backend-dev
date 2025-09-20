package ru.fensy.dev.graphql.controller.user

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.usecase.user.SetInterestsToUserUseCase

@Controller
class SetUserInterestsGraphQLController(
    private val setInterestsToUserUseCase: SetInterestsToUserUseCase,
) {

    @MutationMapping("setInterestsToUser")
    suspend fun setInterestsToUser(@Argument interestIds: List<Long>): UserResponse {
        return setInterestsToUserUseCase.execute(interestIds)
    }
}