package ru.fensy.dev.service.jwt

import java.time.Duration
import java.time.Instant
import java.util.UUID
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import ru.fensy.dev.constants.Constants.JTI_CLAIM_NAME
import ru.fensy.dev.domain.User
import ru.fensy.dev.extensions.sha256
import ru.fensy.dev.repository.RefreshTokenRepository

class JwtService(
    private val issuer: String,
    private val ttl: Duration,
    val refreshTokenTtl: Duration,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    suspend fun generateToken(user: User): GenerateTokenOperationRs {

        val jti = UUID.randomUUID().toString()
        val refreshToken = UUID.randomUUID().toString()

        val claimsSet = JwtClaimsSet.builder()
            .subject(user.username)
            .issuer(issuer)
            .claim(JTI_CLAIM_NAME, jti)
            .expiresAt(Instant.now().plus(ttl))
            .build()

        val jwt = "Bearer ${jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).tokenValue}"

        refreshTokenRepository.save(
            userId = user.id!!,
            tokenHash = refreshToken.sha256(),
            jwtId = jti,
            expiresAt = refreshTokenTtl
        )

        return GenerateTokenOperationRs(jwt = jwt, refresh = refreshToken)
    }

    fun validateToken(token: String): Jwt {
        return jwtDecoder.decode(token.substring(BEARER_SUBSTRING_INDEX))
    }

    companion object {
        private const val BEARER_SUBSTRING_INDEX = "Bearer".length + 1
    }
}

data class GenerateTokenOperationRs(
    val jwt: String,
    val refresh: String,
)

