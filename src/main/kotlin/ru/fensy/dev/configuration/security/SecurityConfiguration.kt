package ru.fensy.dev.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { auth: AuthorizeExchangeSpec ->
                auth
                    .pathMatchers("/test").authenticated()
                    .anyExchange().permitAll()

            }
            .oauth2Login(Customizer.withDefaults())
            .build()
    }

}
