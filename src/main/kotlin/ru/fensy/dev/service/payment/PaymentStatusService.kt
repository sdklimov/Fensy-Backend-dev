package ru.fensy.dev.service.payment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.repository.PaymentRepository
import ru.fensy.dev.repository.SubscriptionsRepository

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
class PaymentStatusService(
    private val paymentRepository: PaymentRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
) {

    suspend fun setStatus(rq: SetStatusRq) {
        paymentRepository.updateStatus(paymentId = rq.paymentId, rq.paymentStatus)
        subscriptionsRepository.updateStatus(rq.subscriptionId, status = rq.subscriptionStatus)
    }

}
