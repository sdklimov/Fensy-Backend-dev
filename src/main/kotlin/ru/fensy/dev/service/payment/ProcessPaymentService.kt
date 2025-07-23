package ru.fensy.dev.service.payment

import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.OffsetDateTime
import org.springframework.stereotype.Service
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.domain.PaymentStatus
import ru.fensy.dev.domain.SubscriptionStatus
import ru.fensy.dev.repository.PaymentRepository
import ru.fensy.dev.repository.SubscriptionsRepository
import ru.fensy.dev.service.subscription.ActivateSubscriptionService

@Service
class ProcessPaymentService(
    private val paymentRepository: PaymentRepository,
    private val verifyPaymentService: VerifyPaymentService,
    private val subscriptionsRepository: SubscriptionsRepository,
    private val activateSubscriptionService: ActivateSubscriptionService,
    private val paymentStatusService: PaymentStatusService,
) {

    private val logger = KotlinLogging.logger { }

    suspend fun execute() {
        val payments = paymentRepository.findAllByStatus(PaymentStatus.PENDING)
        println("Найдено: ${payments.size}")
        payments
            .forEach { p ->
                //todo: тут когда-нибудь добавить многопоточку
                processPayment(p)
            }

    }

    private suspend fun processPayment(payment: Payment) {
        runCatching {
            paymentStatusService.setStatus(
                rq = SetStatusRq(
                    paymentId = payment.id!!,
                    subscriptionId = payment.subscriptionId,
                    paymentStatus = PaymentStatus.IN_PROCESSING,
                    subscriptionStatus = SubscriptionStatus.PAYMENT_IN_PROCESSING,
                )
            )

            if (OffsetDateTime.now() > payment.validUntil) {
                paymentStatusService.setStatus(
                    rq = SetStatusRq(
                        paymentId = payment.id,
                        subscriptionId = payment.subscriptionId,
                        paymentStatus = PaymentStatus.EXPIRED,
                        subscriptionStatus = SubscriptionStatus.PAYMENT_EXPIRED,
                    )
                )
                return
            }

            kotlin.runCatching {
                verifyPaymentService.verify(payment)
            }
                .onFailure {
                    logger.error { it }
                    paymentStatusService.setStatus(
                        rq = SetStatusRq(
                            paymentId = payment.id,
                            subscriptionId = payment.subscriptionId,
                            paymentStatus = PaymentStatus.ERROR,
                            subscriptionStatus = SubscriptionStatus.PAYMENT_PROCESSING_ERROR,
                        )
                    )
                }

            kotlin.runCatching { verifyPaymentService.verify(payment) }
                .onFailure {
                    paymentStatusService.setStatus(
                        rq = SetStatusRq(
                            paymentId = payment.id,
                            subscriptionId = payment.subscriptionId,
                            paymentStatus = PaymentStatus.PENDING,
                            subscriptionStatus = SubscriptionStatus.PENDING,
                        )
                    )
                }
                .onSuccess {
                    activateSubscriptionService.activate(payment)
                }
        }
            .onFailure {
                logger.error(it) { "Ошибка обработки платежа [$payment]" }
                paymentStatusService.setStatus(
                    rq = SetStatusRq(
                        paymentId = payment.id!!,
                        subscriptionId = payment.subscriptionId,
                        paymentStatus = PaymentStatus.ERROR,
                        subscriptionStatus = SubscriptionStatus.PAYMENT_PROCESSING_ERROR,
                    )
                )
            }


    }

}

data class SetStatusRq(
    val paymentId: Long,
    val subscriptionId: Long,
    val paymentStatus: PaymentStatus,
    val subscriptionStatus: SubscriptionStatus,
)