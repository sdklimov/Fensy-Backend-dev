package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Payment(
    val id: Long? = null,
    val uniqueId: UUID,
    val subscriptionId: Long,
    val amountCents: Int,
    val currency: Currency = Currency.TON,
    val paymentMethod: PaymentMethod,
    val paidAt: OffsetDateTime? = null,
    val status: PaymentStatus,
    val validUntil: OffsetDateTime,
)

enum class Currency {
    TON
}

enum class PaymentMethod {
    CARD, APPLE_PAY, CRYPTO
}

enum class PaymentStatus {
    PENDING, SUCCEEDED, IN_PROCESSING, ERROR, EXPIRED
}
