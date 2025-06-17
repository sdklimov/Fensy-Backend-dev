package ru.fensy.dev.usecase.collections

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.exception.UserCollectionAlreadyExistsException
import ru.fensy.dev.graphql.controller.post.input.CreateCollectionRq
import ru.fensy.dev.graphql.controller.post.response.CollectionResponse
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.repository.CreateUserCollectionQueryRq
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class CreateCollectionUseCase(
    private val collectionRepository: CollectionRepository,
) : BaseUseCase() {

    suspend fun execute(input: CreateCollectionRq): CollectionResponse {
        val user = currentUser(true)!!

        if (collectionRepository.checkCollectionExistsWithTitle(title = input.title, userId = user.id!!)){
            throw UserCollectionAlreadyExistsException(message = "Коллекция [${input.title}] уже существует")
        }

        val createdCollection = collectionRepository.createUserCollection(
            rq = CreateUserCollectionQueryRq(
                userId = user.id,
                title = input.title,
                description = input.description,
                allowViewingFor = input.allowViewingFor
            )
        )

        return CollectionResponse(
            collection = createdCollection,
            message = "Коллекция создана успешно",
            success = true
        )
    }

}
