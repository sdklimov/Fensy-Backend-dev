package ru.fensy.dev.usecase.languages

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.LanguagesResponse
import ru.fensy.dev.repository.LanguagesRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional(readOnly = true)
class GetAllLanguagesUseCase(
    private val languagesRepository: LanguagesRepository,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): LanguagesResponse {

        return LanguagesResponse(languages = languagesRepository.getAll(), message = "Языки получены", success = true)
    }

}
