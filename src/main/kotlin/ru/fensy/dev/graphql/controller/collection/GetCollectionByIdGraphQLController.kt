package ru.fensy.dev.graphql.controller.collection

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.CollectionResponse
import ru.fensy.dev.usecase.collections.GetCollectionByIdUseCase

@Controller
class GetCollectionByIdGraphQLController(
    private val getCollectionByIdUseCase: GetCollectionByIdUseCase,
) {

    @QueryMapping("getCollection")
    suspend fun createCollection(
        env: DataFetchingEnvironment,
    ): CollectionResponse {
        return getCollectionByIdUseCase.execute(env)
    }

}
