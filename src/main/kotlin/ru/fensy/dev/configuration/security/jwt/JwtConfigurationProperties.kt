package ru.fensy.dev.configuration.security.jwt

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.security.jwt")
data class JwtConfigurationProperties(
    val privateKey: RSAPrivateKey,
    val publicKey: RSAPublicKey,
    val ttl: Duration,
    val refreshTokenTtl: Duration
)