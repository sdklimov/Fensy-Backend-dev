package ru.fensy.dev.graphql.controller.languages

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.LanguagesResponse
import ru.fensy.dev.usecase.languages.GetAllLanguagesUseCase

@Controller
class GetAllILanguagesGraphQLController(
    private val getAllLanguagesUseCase: GetAllLanguagesUseCase,
) {

    @QueryMapping("getAllLanguages")
    suspend fun getAllInterests(
        env: DataFetchingEnvironment,
    ): LanguagesResponse {
        return getAllLanguagesUseCase.execute(env)
    }

}
