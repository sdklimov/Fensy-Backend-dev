package ru.fensy.dev.graphql.controller.userprofile

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.user.response.UserResponse
import ru.fensy.dev.graphql.controller.userprofile.input.UserProfileInput
import ru.fensy.dev.usecase.profile.UpdateUserProfileUseCase

@Controller
class UpdateUserProfileGraphQLController(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) {

    @MutationMapping("updateUserProfile")
    suspend fun updateUserProfile(@Argument userProfile: UserProfileInput): UserResponse {
        return updateUserProfileUseCase.execute(userProfile)
    }

}
