package ru.fensy.dev.configuration.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Value("\${application.frontend.url}")
    private lateinit var frontendUrl: String

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { it.configurationSource{
                val corsConfiguration = CorsConfiguration()
                corsConfiguration.setAllowedOriginPatterns(
                    listOf(
                        "http://localhost:*",
                        frontendUrl
                    )
                )
                corsConfiguration.allowedMethods =
                    listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                corsConfiguration.allowedHeaders = listOf("*")
                corsConfiguration.allowCredentials = true
                corsConfiguration
            } }
            .csrf {it.disable()}
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange { auth: AuthorizeExchangeSpec ->
                auth
                    .anyExchange().permitAll()
            }
            .build()
    }

}
