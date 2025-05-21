package ru.fensy.dev.configuration.security.webclient

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Конфигурация клиентов получения информации о пользователе
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(UserInfoWebClientProperties::class)
class UserInfoWebClientConfiguration(
    private val userInfoWebClientProperties: UserInfoWebClientProperties,
) {

    @Bean
    fun yandexUserInfoProxyClient(
        webClientBuilder: WebClient.Builder,
    ): WebClient = webClientBuilder
        .baseUrl(userInfoWebClientProperties.yandex)
        .build()
}
