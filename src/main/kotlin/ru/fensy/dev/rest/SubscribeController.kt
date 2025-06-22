package ru.fensy.dev.rest

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.usecase.subscriptions.MakeSubscriptionUseCase

/**
 * Подписаться
 */
@RestController
@RequestMapping(path = ["/api/v1/users/subscriptions"])
class SubscribeController(
    private val makeSubscriptionUseCase: MakeSubscriptionUseCase,
) {

    @PostMapping
    suspend fun execute(
        @RequestBody rq: CreateSubscriptionRq,
    ) {
        makeSubscriptionUseCase.execute(rq.targetId)
    }

}

data class CreateSubscriptionRq(
    val targetId: Long,
)