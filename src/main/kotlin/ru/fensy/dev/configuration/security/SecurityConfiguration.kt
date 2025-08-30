package ru.fensy.dev.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { corsSpec ->
                corsSpec.configurationSource {
                    CorsConfiguration().apply {
                        addAllowedOriginPattern("*")
                        addAllowedMethod("*")
                        addAllowedHeader("*")
                        allowCredentials = true
                    }
                }
            }
            .csrf {it.disable().build()}
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange { auth: AuthorizeExchangeSpec ->
                auth
                    .anyExchange().permitAll()
            }
            .oauth2Login(Customizer.withDefaults())
            .build()
    }

}
