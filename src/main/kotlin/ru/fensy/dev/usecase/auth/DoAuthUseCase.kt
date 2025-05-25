package ru.fensy.dev.usecase.auth

import graphql.schema.DataFetchingEnvironment
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseCookie.ResponseCookieBuilder
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import ru.fensy.dev.auth.provider.AuthProvider
import ru.fensy.dev.graphql.controller.auth.response.AuthResponse
import ru.fensy.dev.service.jwt.JwtService

/**
 * Выполнить авторизацию
 */
@Component
@Transactional
class DoAuthUseCase(
    authProviders: List<AuthProvider>,
    private val jwtService: JwtService,
) {

    private val providerByName = authProviders.associateBy { it.name() }

    suspend fun execute(env: DataFetchingEnvironment): AuthResponse {

        val providerName = env.getArgument<String>("provider") ?: "__PROVIDER_NOT_FOUND"
        val accessToken = env.getArgument<String>("accessToken") ?: return TOKEN_NOT_FOUND_RESPONSE

        return providerByName[providerName]?.let { provider ->
            val authResult = provider.auth(accessToken)
            val tokenRs = jwtService.generateToken(authResult.user)

            env.graphQlContext.get<ServerWebExchange>(ServerWebExchange::class.java)
                .response
                .addCookie(
                    ResponseCookie.from("refreshToken", tokenRs.refresh)
                        .secure(true)
                        .httpOnly(true)
                        .build()
                )

            return@let AuthResponse(
                created = authResult.isUserCreated,
                accessToken = tokenRs.jwt,
                message = when (authResult.isUserCreated) {
                    true -> "Пользователь успешно зарегистрирован"
                    false -> "Пользователь успешно авторизован"
                },
                success = true
            )
        } ?: PROVIDER_NOT_FOUND_RESPONSE
    }

    companion object {
        private val TOKEN_NOT_FOUND_RESPONSE = AuthResponse(
            created = false,
            accessToken = null,
            message = "Access token could not be retrieved",
            success = false
        )

        private val PROVIDER_NOT_FOUND_RESPONSE =
            AuthResponse(created = false, message = "Провайдер не найден", success = false)

    }

}
