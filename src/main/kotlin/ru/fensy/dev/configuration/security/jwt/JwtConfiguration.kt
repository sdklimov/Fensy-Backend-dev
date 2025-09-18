package ru.fensy.dev.configuration.security.jwt

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import ru.fensy.dev.repository.RefreshTokenRepository
import ru.fensy.dev.service.jwt.JwtService


@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(JwtConfigurationProperties::class)
class JwtConfiguration(
    private val properties: JwtConfigurationProperties,
) {

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = RSAKey.Builder(properties.publicKey)
            .privateKey(properties.privateKey).build()

        return NimbusJwtEncoder(
            ImmutableJWKSet(JWKSet(jwk))
        )
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(properties.publicKey).build()
    }

    @Bean
    fun jwtService(
        @Value("\${spring.application.name}")
        appName: String,
        jwtEncoder: JwtEncoder,
        jwtDecoder: JwtDecoder,
        refreshTokenRepository: RefreshTokenRepository,
    ): JwtService = JwtService(
        issuer = appName,
        ttl = properties.ttl,
        refreshTokenTtl = properties.refreshTokenTtl,
        jwtEncoder = jwtEncoder,
        jwtDecoder = jwtDecoder,
        refreshTokenRepository = refreshTokenRepository
    )


}
