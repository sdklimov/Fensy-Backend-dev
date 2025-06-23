package ru.fensy.dev.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.rest.domain.GetSubscriptionsViewRs
import ru.fensy.dev.usecase.subscriptions.GetSubscriptionsUseCase

/**
 * Подписаться
 */
@RestController
@RequestMapping(path = ["/api/v1/users/subscriptions"])
class GetSubscriptionsController(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
) {

    @GetMapping
    suspend fun execute(
        @RequestParam(required = false) pageNumber: Int = 1,
    ): GetSubscriptionsViewRs {
       return getSubscriptionsUseCase.execute(pageNumber)
    }

}
