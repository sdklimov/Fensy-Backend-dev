package ru.fensy.dev.usecase.collections

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.graphql.controller.post.response.CollectionsResponse
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class GetAllUserCollectionsUseCase(
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): CollectionsResponse {

        val username = env.getArgument<String>("username")
        val user = username?.let { userRepository.findByUsername(username) } ?: currentUser(true)!!
        return CollectionsResponse(
            collections = collectionRepository.findByUserId(userId = user.id!!),
            message = "Коллекции пользователя получены",
            success = true
        )

    }

}