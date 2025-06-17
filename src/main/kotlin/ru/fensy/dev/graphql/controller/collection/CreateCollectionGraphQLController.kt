package ru.fensy.dev.graphql.controller.collection

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.input.CreateCollectionRq
import ru.fensy.dev.graphql.controller.post.response.CollectionResponse
import ru.fensy.dev.usecase.collections.CreateCollectionUseCase

@Controller
class CreateCollectionGraphQLController(
    private val createCollectionUseCase: CreateCollectionUseCase,
) {

    @MutationMapping("createCollection")
    suspend fun createCollection(
        @Argument input: CreateCollectionRq,
    ): CollectionResponse {
        return createCollectionUseCase.execute(input)
    }

}