package ru.fensy.dev.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Subscription(
    val id: Long,
//    val uniqueId: UUID,
    val subscriberId: Long,
    val targetId: Long,
    val subscriptionType: SubscriptionType,
    val subscriptionStatus: SubscriptionStatus,
    val startedAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)


enum class SubscriptionStatus {
    ACTIVE, EXPIRED, CANCELED, PENDING, PAYMENT_IN_PROCESSING, PAYMENT_EXPIRED, PAYMENT_PROCESSING_ERROR
}

enum class SubscriptionType {
    MONTHLY, YEARLY
}
