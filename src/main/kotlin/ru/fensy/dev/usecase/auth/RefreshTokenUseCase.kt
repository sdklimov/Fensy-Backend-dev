package ru.fensy.dev.usecase.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.exception.AccessDeniedException
import ru.fensy.dev.extensions.sha256
import ru.fensy.dev.graphql.controller.auth.response.RefreshResponse
import ru.fensy.dev.repository.RefreshTokenRepository
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.service.jwt.JwtService
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class RefreshTokenUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
) : BaseUseCase() {

    @Value("\${application.frontend.domain}")
    private lateinit var frontendDomain: String

    suspend fun execute(env: DataFetchingEnvironment): RefreshResponse {
        val exchange = env.graphQlContext.get<ServerWebExchange>(ServerWebExchange::class.java)
        val refreshToken = exchange.request.cookies.getFirst("refreshToken")?.value
            ?: throw org.springframework.security.access.AccessDeniedException("Refresh токен не найден")
        val refreshTokenEntity = refreshTokenRepository.getRefreshToken(tokenHash = refreshToken.sha256())
            ?: throw AccessDeniedException()

        val user = userRepository.findById(refreshTokenEntity.userId)
        val tokens = jwtService.generateToken(user)

        refreshTokenRepository.revokeToken(refreshTokenEntity.id!!)

        env.graphQlContext.get<ServerWebExchange>(ServerWebExchange::class.java)
            .response
            .addCookie(
                ResponseCookie.from("refreshToken", tokens.refresh)
                    .secure(true)
                    .path("/")
                    .maxAge(jwtService.refreshTokenTtl)
                    .sameSite("None")
                    .domain(".${frontendDomain}")
                    .httpOnly(true)
                    .build()
            )

        return RefreshResponse(accessToken = tokens.jwt, message = "Токен успешно обновлен", success = true)
    }

}
