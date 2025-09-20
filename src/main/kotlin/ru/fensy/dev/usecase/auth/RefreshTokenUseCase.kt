package ru.fensy.dev.usecase.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.constants.Constants.JTI_CLAIM_NAME
import ru.fensy.dev.exception.AccessDeniedException
import ru.fensy.dev.extensions.sha256
import ru.fensy.dev.graphql.controller.auth.response.RefreshResponse
import ru.fensy.dev.repository.RefreshTokenRepository
import ru.fensy.dev.service.jwt.JwtService
import ru.fensy.dev.usecase.BaseUseCase

@Component
@Transactional
class RefreshTokenUseCase(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService,
) : BaseUseCase() {

    suspend fun execute(env: DataFetchingEnvironment): RefreshResponse {
        val user = currentUser(required = true)!!
        val refreshToken = env.getArgument<String>("refreshToken")!!
        val jti = getJwtClaims()[JTI_CLAIM_NAME] as String
        val tokens = refreshTokenRepository.getRefreshToken(userId = user.id!!, jti = jti, tokenHash = refreshToken.sha256())
            ?.let {
                jwtService.generateToken(user)
            }?: throw AccessDeniedException()

        env.graphQlContext.get<ServerWebExchange>(ServerWebExchange::class.java)
            .response
            .addCookie(
                ResponseCookie.from("refreshToken", tokens.refresh)
                    .secure(true)
                    .httpOnly(true)
                    .build()
            )

        return RefreshResponse(accessToken = tokens.jwt, message = "Токен успешно обновлен", success = true)
    }

}
