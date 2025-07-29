package ru.fensy.dev.usecase.subscriptions

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Currency
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.domain.PaymentMethod
import ru.fensy.dev.domain.PaymentStatus
import ru.fensy.dev.domain.SubscriptionStatus
import ru.fensy.dev.domain.SubscriptionType
import ru.fensy.dev.repository.CreateSubscriptionRq
import ru.fensy.dev.repository.PaymentRepository
import ru.fensy.dev.repository.SubscriptionsRepository
import ru.fensy.dev.rest.StartSubscriptionViewRs
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class MakeSubscriptionUseCase(
    private val subscriptionsRepository: SubscriptionsRepository,
    private val paymentRepository: PaymentRepository,
) : BaseUseCase() {


    suspend fun execute(targetUserId: Long, subscriptionType: SubscriptionType): StartSubscriptionViewRs {
        val currentUser = currentUser(true)!!
        val startedAt = OffsetDateTime.now()
        val expiresAt = when (subscriptionType) {
            SubscriptionType.MONTHLY -> startedAt.plusMonths(1)
            SubscriptionType.YEARLY -> startedAt.plusYears(1)
        }

        val subId = subscriptionsRepository.createSubscription(
            CreateSubscriptionRq(
                subscriberId = currentUser.id!!,
                targetId = targetUserId,
                subscriptionType = subscriptionType,
                status = SubscriptionStatus.PENDING,
                startedAt = startedAt,
                expiresAt = expiresAt,
            )
        )

        val paymentUniqueId = UUID.randomUUID()
        val payment = Payment(
            uniqueId = paymentUniqueId,
            subscriptionId = subId,
            //todo: Вынести стоимость подписки в env
            amountCents = 0.1,
            currency = Currency.TON,
            paymentMethod = PaymentMethod.CRYPTO,
            status = PaymentStatus.PENDING,
            validUntil = OffsetDateTime.now().plusDays(1) //todo: Вынести в env
        )

        paymentRepository.create(payment)
        return StartSubscriptionViewRs(
            subscriptionUniqueId = paymentUniqueId,
            currency = payment.currency.name,
            amount = BigDecimal.valueOf(payment.amountCents),
            paymentMethod = payment.paymentMethod.name
        )
    }

}
