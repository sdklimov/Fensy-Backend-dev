package ru.fensy.dev.usecase.countries

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.CountriesResponse
import ru.fensy.dev.repository.CountriesRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional(readOnly = true)
class GetAllCountriesUseCase(
    private val countriesRepository: CountriesRepository,
) : BaseUseCase() {

    suspend fun execute(): CountriesResponse {
        return CountriesResponse(countries = countriesRepository.findAll(), message = "Страны получены", success = true)
    }

}
