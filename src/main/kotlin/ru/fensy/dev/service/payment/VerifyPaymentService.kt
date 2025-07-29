package ru.fensy.dev.service.payment

import org.springframework.stereotype.Service
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.service.payment.ton.TonService

@Service
class VerifyPaymentService(
    private val tonService: TonService,
) {

    suspend fun verify(payment: Payment): Boolean {
        val transactions = tonService.get(100)
//        transactions.map { it.in_msg_field.message }
//        transactions.firstOrNull {it.in_msg.message == payment.uniqueId.toString()}

        val validTr =
            transactions.firstOrNull { it.in_msg.message == payment.uniqueId.toString() } ?: return false

        val amount = validTr.in_msg.value.toDouble() / 1_000_000_000

        if (amount != payment.amountCents) {
            error("Переведенная сумма не соответствует ожидаемой")
        }

        return true
    }

}
