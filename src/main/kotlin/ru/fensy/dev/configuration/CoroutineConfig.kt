package ru.fensy.dev.configuration

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.reactive.TransactionalEventPublisher

@Configuration
class CoroutineConfig {

    @Bean("applicationScope")
    fun applicationScope(): CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.IO + CoroutineName("ApplicationScope")
        )
    }

    @Bean
    fun transactionalEventPublisher(
        applicationEventPublisher: ApplicationEventPublisher
    ): TransactionalEventPublisher {
        return TransactionalEventPublisher(applicationEventPublisher)
    }
}