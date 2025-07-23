package ru.fensy.dev.service.subscription

import java.math.BigDecimal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.domain.PaymentStatus
import ru.fensy.dev.domain.SubscriptionStatus
import ru.fensy.dev.repository.SubscriptionsRepository
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.service.payment.PaymentStatusService
import ru.fensy.dev.service.payment.SetStatusRq
import ru.fensy.dev.service.payment.ton.TonService

@Service
@Transactional
class ActivateSubscriptionService(
    private val subscriptionsRepository: SubscriptionsRepository,
    private val paymentStatusService: PaymentStatusService,
    private val tonService: TonService,
    private val userRepository: UserRepository,
) {

    suspend fun activate(payment: Payment) {
        paymentStatusService.setStatus(
            SetStatusRq(
                paymentId = payment.id!!,
                subscriptionId = payment.subscriptionId,
                paymentStatus = PaymentStatus.SUCCEEDED,
                subscriptionStatus = SubscriptionStatus.ACTIVE,
            )
        )
        subscriptionsRepository.updateStartedAt(payment.subscriptionId)

        val subscription = subscriptionsRepository.getById(payment.subscriptionId)!!
        val targetTonWallet = userRepository.findById(subscription.targetId).tonWalletId!!

        val amountToSend = BigDecimal.valueOf(payment.amountCents) * BigDecimal.valueOf(0.8) //todo: Вынести процент дани в env
        tonService.send(targetWalletId = targetTonWallet, amountTon = amountToSend.toDouble(), comment = "Оплата подписки")

    }

}
