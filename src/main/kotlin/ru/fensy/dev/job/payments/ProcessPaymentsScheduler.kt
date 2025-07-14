package ru.fensy.dev.job.payments

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import ru.fensy.dev.service.payment.ProcessPaymentService

@Configuration(proxyBeanMethods = false)
class ProcessPaymentsScheduler(
    private val processPaymentService: ProcessPaymentService,
) {

    // todo: Вынести fixedRate в ENV
    @Scheduled(fixedRate = 3000)
    suspend fun execute() {
        processPaymentService.execute()
    }
}
