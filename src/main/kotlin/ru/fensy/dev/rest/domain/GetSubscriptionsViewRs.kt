package ru.fensy.dev.rest.domain

import ru.fensy.dev.domain.Subscription
import ru.fensy.dev.rest.domain.common.PagingInfo

data class GetSubscriptionsViewRs(
    val result: List<Subscription>,
    val paging: PagingInfo,
)
