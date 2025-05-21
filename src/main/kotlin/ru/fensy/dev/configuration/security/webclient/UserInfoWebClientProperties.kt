package ru.fensy.dev.configuration.security.webclient

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.oauth2.user-info-uri")
data class UserInfoWebClientProperties(
    val yandex: String
)
