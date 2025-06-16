package ru.fensy.dev.graphql.controller.collection

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.CollectionsResponse
import ru.fensy.dev.usecase.collections.GetAllUserCollectionsUseCase

@Controller
class GetAllUserCollectionsGraphQLController(
    private val getAllUserCollectionsUseCase: GetAllUserCollectionsUseCase,
) {

    @QueryMapping("getAllUserCollections")
    suspend fun getAllUserCollections(
        env: DataFetchingEnvironment,
    ): CollectionsResponse {
        val res = getAllUserCollectionsUseCase.execute(env)
        return res
    }

}
