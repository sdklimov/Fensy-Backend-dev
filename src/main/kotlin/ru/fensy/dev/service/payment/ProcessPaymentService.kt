package ru.fensy.dev.service.payment

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.OffsetDateTime
import org.springframework.stereotype.Service
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.domain.PaymentStatus
import ru.fensy.dev.domain.SubscriptionStatus
import ru.fensy.dev.repository.PaymentRepository
import ru.fensy.dev.repository.SubscriptionsRepository

@Service
class ProcessPaymentService(
    private val paymentRepository: PaymentRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
) {

    private val logger = KotlinLogging.logger { }

    suspend fun execute() {
        val payments = paymentRepository.findAllByStatus(PaymentStatus.PENDING)
        println("Найдено: ${payments.size}")
        payments
            .forEach { p ->
                kotlin.runCatching {
                    processPayment(p)
                }
                    .onFailure {
                        logger.error(it) { "Ошибка обработки платежа [$p]" }
                        setStatus(
                            rq = SetStatusRq(
                                paymentId = p.id!!,
                                subscriptionId = p.subscriptionId,
                                paymentStatus = PaymentStatus.ERROR,
                                subscriptionStatus = SubscriptionStatus.PAYMENT_PROCESSING_ERROR,
                            )
                        )
                    }
            }

    }

    private suspend fun processPayment(payment: Payment) {
        setStatus(
            rq = SetStatusRq(
                paymentId = payment.id!!,
                subscriptionId = payment.subscriptionId,
                paymentStatus = PaymentStatus.IN_PROCESSING,
                subscriptionStatus = SubscriptionStatus.PAYMENT_IN_PROCESSING,
            )
        )

        if (OffsetDateTime.now() > payment.validUntil) {
            setStatus(
                rq = SetStatusRq(
                    paymentId = payment.id,
                    subscriptionId = payment.subscriptionId,
                    paymentStatus = PaymentStatus.EXPIRED,
                    subscriptionStatus = SubscriptionStatus.PAYMENT_EXPIRED,
                )
            )
            return
        }

        activateSubscription(payment)

    }

    private suspend fun setStatus(rq: SetStatusRq) {
        paymentRepository.updateStatus(paymentId = rq.paymentId, rq.paymentStatus)
        subscriptionsRepository.updateStatus(rq.subscriptionId, status = rq.subscriptionStatus)
    }

    private suspend fun activateSubscription(payment: Payment) {
        setStatus(
            rq = SetStatusRq(
                paymentId = payment.id!!,
                subscriptionId = payment.subscriptionId,
                paymentStatus = PaymentStatus.SUCCEEDED,
                subscriptionStatus = SubscriptionStatus.ACTIVE,
            )
        )

        subscriptionsRepository.updateStartedAt(payment.subscriptionId)
    }


}

data class SetStatusRq(
    val paymentId: Long,
    val subscriptionId: Long,
    val paymentStatus: PaymentStatus,
    val subscriptionStatus: SubscriptionStatus,
)