package ru.fensy.dev.service.payment

import org.springframework.stereotype.Service
import ru.fensy.dev.domain.Payment

@Service
class VerifyPaymentService() {

    suspend fun verify(payment: Payment): Boolean {

        return false
    }

}
