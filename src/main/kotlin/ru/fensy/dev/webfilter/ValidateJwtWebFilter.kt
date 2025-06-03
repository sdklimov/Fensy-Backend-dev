package ru.fensy.dev.webfilter

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ru.fensy.dev.constants.CURRENT_USER_CONTEXT_KEY
import ru.fensy.dev.constants.JWT_CLAIMS
import ru.fensy.dev.exception.UserNotFoundException
import ru.fensy.dev.repository.UserRepository
import ru.fensy.dev.service.jwt.JwtService

@Component
class ValidateJwtWebFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
) : WebFilter {


    private val logger = KotlinLogging.logger { }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val jwt = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?: return chain.filter(exchange)

        val claims = runCatching {
            jwtService.validateToken(jwt).claims
        }
            .fold(
                onSuccess = { it },
                onFailure = {
                    logger.error(it) { "Ошибка валидации JWT-токена" }
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    return exchange.response.setComplete()
                }
            )

        return mono {
            val userName = claims.get("sub") as String
            val user = userRepository.findByUsername(userName)
                ?: throw UserNotFoundException("Пользователь [$userName] не найден")
            user
        }
            .flatMap { user ->
                return@flatMap chain.filter(exchange)
                    .contextWrite { context ->
                        context.putAllMap(
                            mapOf(
                                CURRENT_USER_CONTEXT_KEY to user,
                                JWT_CLAIMS to claims
                            )
                        )
                    }
            }
            .then()
    }
}
