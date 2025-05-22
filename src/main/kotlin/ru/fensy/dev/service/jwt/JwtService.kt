package ru.fensy.dev.service.jwt

import java.time.Duration
import java.time.Instant
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters

class JwtService(
    private val issuer: String,
    private val ttl: Duration,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
) {
    fun generateToken(username: String): String {
        val claimsSet = JwtClaimsSet.builder()
            .subject(username)
            .issuer(issuer)
            .expiresAt(Instant.now().plus(ttl))
            .build()
        return "Bearer ${jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).tokenValue}"
    }

    fun validateToken(token: String): Jwt {
        return jwtDecoder.decode(token.substring(BEARER_SUBSTRING_INDEX))
    }

    companion object {
        private const val BEARER_SUBSTRING_INDEX = "Bearer".length + 1
    }
}
