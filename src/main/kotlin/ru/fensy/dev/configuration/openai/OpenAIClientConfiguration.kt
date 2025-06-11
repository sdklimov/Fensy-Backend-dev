package ru.fensy.dev.configuration.openai

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.reactive.function.client.WebClient

@Configuration(proxyBeanMethods = false)
class OpenAIClientConfiguration(
    @Value("\${application.content-moderation.openai.key}")
    private val key: String,
) {

    @Bean
    fun openAIClient(
        webClientBuilder: WebClient.Builder,
    ) = webClientBuilder
        .baseUrl("https://api.openai.com/v1")
        .defaultHeaders {
            it.add(HttpHeaders.AUTHORIZATION, "Bearer $key")
            it.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
        }
        .build()

}
