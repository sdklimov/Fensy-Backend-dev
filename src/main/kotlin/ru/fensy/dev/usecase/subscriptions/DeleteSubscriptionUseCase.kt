package ru.fensy.dev.usecase.subscriptions

import org.springframework.stereotype.Component
import ru.fensy.dev.repository.SubscriptionsRepository
import ru.fensy.dev.usecase.BaseUseCase

@Component
class DeleteSubscriptionUseCase(
    private val subscriptionsRepository: SubscriptionsRepository,
): BaseUseCase() {


    suspend fun execute(targetUserId: Long) {
        val currentUser = currentUser(true)
        subscriptionsRepository.unsubscribe(subscriberId = currentUser!!.id!!, targetId = targetUserId)
    }

}
