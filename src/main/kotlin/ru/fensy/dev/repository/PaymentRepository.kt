package ru.fensy.dev.repository

import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Currency
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.domain.PaymentMethod
import ru.fensy.dev.domain.PaymentStatus

@Component
class PaymentRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun create(payment: Payment): Long =
        databaseClient
            .sql("""
                insert into payments(unique_id, subscription_id, amount_cents, currency, payment_method, status, valid_until)
                values (:uniqueId, :subscriptionId, :amountCents, :currency, :paymentMethod, :status, :validUntil)
                returning id
            """.trimIndent())
            .bind("uniqueId", payment.uniqueId)
            .bind("subscriptionId", payment.subscriptionId)
            .bind("amountCents", payment.amountCents)
            .bind("currency", payment.currency.name)
            .bind("paymentMethod", payment.paymentMethod.name)
            .bind("status", payment.status.name)
            .bind("validUntil", payment.validUntil)
            .fetch()
            .one()
            .map { it["id"] as Long }
            .awaitSingle()


    suspend fun findAllByStatus(status: PaymentStatus): List<Payment> =
        databaseClient
            .sql("""
                select * from payments where status = :status
            """.trimIndent())
            .bind("status", status.name)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    suspend fun updateStatus(paymentId: Long, status: PaymentStatus) {
        databaseClient
            .sql("""
                update payments set status = :status, updated_at = now() where id = :id
            """.trimIndent())
            .bind("id", paymentId)
            .bind("status", status.name)
            .fetch()
            .awaitRowsUpdated()
    }

    private fun of(source: Map<String, Any>): Payment {
        return Payment(
            id = source["id"] as Long,
            uniqueId = source["unique_id"] as UUID,
            subscriptionId = source["subscription_id"] as Long,
            amountCents = source["amount_cents"] as Int,
            currency = Currency.valueOf(source["currency"] as String),
            paymentMethod = PaymentMethod.valueOf(source["payment_method"] as String),
            status = PaymentStatus.valueOf(source["status"] as String),
            validUntil = source["valid_until"] as OffsetDateTime,
        )
    }
}
