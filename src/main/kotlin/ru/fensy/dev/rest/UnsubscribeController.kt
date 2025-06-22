package ru.fensy.dev.rest

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.usecase.subscriptions.DeleteSubscriptionUseCase

/**
 * Подписаться
 */
@RestController
@RequestMapping(path = ["/api/v1/users/subscriptions"])
class UnsubscribeController(
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase,
) {

    @DeleteMapping
    suspend fun execute(
        @RequestParam targetId: Long,
    ) {
        deleteSubscriptionUseCase.execute(targetId)
    }

}
