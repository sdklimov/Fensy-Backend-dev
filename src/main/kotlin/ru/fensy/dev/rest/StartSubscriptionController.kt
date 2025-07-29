package ru.fensy.dev.rest

import java.math.BigDecimal
import java.util.UUID
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.domain.SubscriptionType
import ru.fensy.dev.usecase.subscriptions.MakeSubscriptionUseCase

/**
 * Подписаться
 */
@RestController
@RequestMapping(path = ["/api/v1/users/subscriptions"])
class StartSubscriptionController(
    private val makeSubscriptionUseCase: MakeSubscriptionUseCase,
) {

    @PostMapping
    suspend fun execute(
        @RequestBody rq: StartSubscriptionRq,
    ): StartSubscriptionViewRs = makeSubscriptionUseCase.execute(rq.targetId, rq.subscriptionType)

}

data class StartSubscriptionRq(
    val targetId: Long,
    val subscriptionType: SubscriptionType,
)

data class StartSubscriptionViewRs(
    val subscriptionUniqueId: UUID,
    val currency: String,
    val amount: BigDecimal,
    val paymentMethod: String,
)