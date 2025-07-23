package ru.fensy.dev.service.payment

import org.springframework.stereotype.Service
import ru.fensy.dev.domain.Payment
import ru.fensy.dev.service.payment.ton.TonService

@Service
class VerifyPaymentService(
    private val tonService: TonService,
) {

    suspend fun verify(payment: Payment) {
        val transactions = tonService.get(100)
        println(transactions)

        val validTr = transactions.firstOrNull { it.out_msgs.any { it.message == payment.uniqueId.toString() } } ?: return

        val amount = validTr.out_msgs?.first()?.value?.toDouble()?.let { it / 1_000_000_000 }
            ?: error("Ошибка получения суммы из транзакции")

        if (amount != payment.amountCents) {
            error("Переведенная сумма не соответствует ожидаемой")
        }
    }

}
