package ru.fensy.dev.service

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Сервис получения информации о пользователе из Yandex
 */
@Service
class YandexUserInfoProxyService(
    private val yandexUserInfoProxyClient: WebClient,
) {

    suspend fun getUserInfo(accessToken: String): Map<String, Any> {
        return yandexUserInfoProxyClient
            .get()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .awaitBody<Map<String, Any>>()
    }

}
