package ru.fensy.dev.configuration.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration(proxyBeanMethods = false)
class CorsGlobalConfiguration {

    /**
     * Бин [CorsWebFilter]
     */
    @Bean
    fun corsFilter(corsConfigurationSource: CorsConfigurationSource): CorsWebFilter {
        return CorsWebFilter(corsConfigurationSource)
    }

    /**
     * Бин [CorsConfigurationSource]
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().applyPermitDefaultValues()
            .also {
                it.addAllowedMethod(CorsConfiguration.ALL)
                it.addAllowedHeader(CorsConfiguration.ALL)
                it.allowCredentials = true
                it.addAllowedOriginPattern(CorsConfiguration.ALL)
            }

        return UrlBasedCorsConfigurationSource()
            .apply { registerCorsConfiguration("/**", config) }
    }

}
