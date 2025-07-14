package ru.fensy.dev.rest.domain

import ru.fensy.dev.domain.SubscriptionViewModel
import ru.fensy.dev.rest.domain.common.PagingInfo

data class GetSubscriptionsViewRs(
    val result: List<SubscriptionViewModel>,
    val paging: PagingInfo,
)
