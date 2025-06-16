package ru.fensy.dev.usecase.interests

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.InterestsResponse
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class GetAllInterestsUseCase(
    private val interestsRepository: InterestsRepository,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): InterestsResponse {
        val languageId = env.getArgument<String>("languageId")?.toLong()

        val interests = languageId?.let {
            interestsRepository.getAllWithTranslation(it)
        } ?: interestsRepository.getAll()

        return InterestsResponse(interests = interests, message = "Интересы получены", success = true)
    }

}
