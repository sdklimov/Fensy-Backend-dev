package ru.fensy.dev.usecase.subscriptions

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.repository.SubscriptionsRepository
import ru.fensy.dev.rest.domain.GetSubscriptionsViewRs
import ru.fensy.dev.rest.domain.common.PagingInfo
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class GetSubscriptionsUseCase(
    private val subscriptionsRepository: SubscriptionsRepository,
) : BaseUseCase() {


    suspend fun execute(pageNumber: Int): GetSubscriptionsViewRs {
        val currentUser = currentUser(true)!!
        //todo: pageSize вынести в props
        val pageRq = PageRequest(pageNumber = pageNumber, pageSize = PAGE_SIZE)
        val subs = subscriptionsRepository.getSubscriptions(currentUser.id!!, pageRq)
        val total = subscriptionsRepository.countSubscriptions(currentUser.id)

        return GetSubscriptionsViewRs(
            result = subs,
            paging = PagingInfo(pageNumber = pageNumber, pageSize = PAGE_SIZE, itemsTotal = total)
        )
    }

    companion object {
        private const val PAGE_SIZE = 15
    }

}
