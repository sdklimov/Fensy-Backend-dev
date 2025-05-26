package ru.fensy.dev.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter

@Configuration(proxyBeanMethods = false)
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    @Profile("!local")
    fun configure(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange { auth: AuthorizeExchangeSpec ->
                auth
                    .anyExchange().permitAll()
            }
            .oauth2Login(Customizer.withDefaults())
            .build()
    }


    @Bean
    @Profile("local")
    fun configureLocal(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .cors {
                it.disable()
            }
            .authorizeExchange { auth: AuthorizeExchangeSpec ->
                auth.anyExchange().permitAll()
            }
            .oauth2Login(Customizer.withDefaults())
            .build()
    }

}
