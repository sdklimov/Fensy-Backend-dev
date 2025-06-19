package ru.fensy.dev.usecase.collections

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.exception.CollectionNotFoundException
import ru.fensy.dev.graphql.controller.post.response.CollectionResponse
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class GetCollectionByIdUseCase(
    private val collectionRepository: CollectionRepository,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): CollectionResponse {

        val collectionId = (((env.arguments as Map<String, Any>).entries.first().value) as String).toLong()
         collectionRepository.findById(collectionId)
            ?.let {
                return CollectionResponse(
                    collection = it,
                    message = "Коллекция получена успешно",
                    success = true
                )
            } ?: throw CollectionNotFoundException.create()

    }

}
