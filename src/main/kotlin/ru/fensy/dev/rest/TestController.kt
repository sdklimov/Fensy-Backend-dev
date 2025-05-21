package ru.fensy.dev.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.fensy.dev.service.YandexUserInfoProxyService

@RestController
class TestController(
    private val yandexUserInfoProxyService: YandexUserInfoProxyService,
) {

    @GetMapping("/test")
    suspend fun test(
        @RequestParam token: String,
    ) = yandexUserInfoProxyService.getUserInfo(token)

}