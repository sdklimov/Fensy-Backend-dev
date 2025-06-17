package ru.fensy.dev.graphql.controller.countries

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.CountriesResponse
import ru.fensy.dev.usecase.countries.GetAllCountriesUseCase

@Controller
class GetAllICountriesGraphQLController(
    private val getAllCountriesUseCase: GetAllCountriesUseCase,
) {

    @QueryMapping("getAllCountries")
    suspend fun getAllCountries(
        env: DataFetchingEnvironment,
    ): CountriesResponse {
        return getAllCountriesUseCase.execute()
    }

}
