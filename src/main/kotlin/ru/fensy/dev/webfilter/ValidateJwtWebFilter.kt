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
import ru.fensy.dev.constants.REQUEST_HTTP_HEADERS
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


        val userName = runCatching {
            val validJwt = jwtService.validateToken(jwt)
            validJwt.claims?.get("sub") as String
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
            val user = userRepository.findByUsername(userName)
                ?: throw UserNotFoundException("Пользователь [$userName] не найден")
            user
        }
            .flatMap { user ->
                return@flatMap chain.filter(exchange)
                    .contextWrite { context ->
                        context.put(CURRENT_USER_CONTEXT_KEY, user)
                    }
            }
            .then()



//        return mono {
//            // если токен есть, то валидируем его и кладем в контекст.
//            val userName = kotlin.runCatching {
//                val jwt = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: run {
//                    return@mono chain.filter(exchange)
//                }
//                val validJwt = jwt.let { jwtService.validateToken(it) }
//                validJwt.claims?.get("sub") as String
//            }.fold(
//                onSuccess = { it },
//                onFailure = {
//                    logger.error(it) { "Ошибка валидации JWT-токена" }
//                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
//                    return@mono exchange.response.setComplete()
//                }
//            )
//        }
//            .then()
//
//            return@defer mono {
//                userName.let { userRepository.findByUsername(it) }
//            }
//                .flatMap { user ->
//                    return@flatMap chain.filter(exchange)
//                        .contextWrite { context ->
//                            context.put(CURRENT_USER_CONTEXT_KEY, user)
//                        }
//                }

    }
//        return Mono.defer {
//            val request = exchange.request
//            val path = request.path.toString()
//
//            if (SWAGGER_PATHS.any { path.startsWith(it) }) {
//                return@defer chain.filter(exchange)
//            }
//
//            if (path != "/gql") {
//                val userName = kotlin.runCatching {
//                    val jwt = request.headers.getFirst(HttpHeaders.AUTHORIZATION)!!
//                    val validJwt = jwtService.validateToken(jwt)
//                    validJwt.claims["sub"] as String
//                }.fold(
//                    onSuccess = { it },
//                    onFailure = {
//                        logger.error(it) { "Ошибка валидации JWT" }
//                        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
//                        return@defer exchange.response.setComplete()
//                    }
//                )
//
//                return@defer mono {
//                    val user = userRepository.findByUsername(userName)
//                        ?: throw UserNotFoundException("Пользователь [$userName] не найден")
//                    user
//                }.flatMap { user ->
//                    chain.filter(exchange)
//                        .contextWrite { context ->
//                            context.put(CURRENT_USER_CONTEXT_KEY, user)
//                        }
//                }
//            }
//            return@defer DataBufferUtils.join(request.body)
//                .flatMap { dataBuffer ->
//                    val bodyBytes = ByteArray(dataBuffer.readableByteCount())
//                    dataBuffer.read(bodyBytes)
//                    DataBufferUtils.release(dataBuffer)
//                    val bodyString = String(bodyBytes, Charsets.UTF_8)
//                    val json = objectMapper.readTree(bodyString)
//                    val query = json["query"].asText()
//                    val requestOperationName = extractOperationName(query)
//
//                    if (!AUTH_REQUIRED_OPERATIONS.contains(requestOperationName)) {
//                        chain.filter(restoreBody(bodyBytes, exchange))
//                    } else {
//                        val userName = kotlin.runCatching {
//                            val jwt = request.headers.getFirst(HttpHeaders.AUTHORIZATION)!!
//                            val validJwt = jwtService.validateToken(jwt)
//                            validJwt.claims["sub"] as String
//                        }.fold(
//                            onSuccess = { it },
//                            onFailure = {
//                                logger.error(it) { "Ошибка валидации JWT" }
//                                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
//                                return@flatMap exchange.response.setComplete()
//                            }
//                        )
//
//                        mono {
//                            userRepository.findByUsername(userName)
//                        }.flatMap { user ->
//                            chain.filter(restoreBody(bodyBytes, exchange))
//                                .contextWrite { context ->
//                                    context.put(CURRENT_USER_CONTEXT_KEY, user)
//                                }
//                        }
//
//                    }
//                }
//
//                .then()
//        }

//    private fun restoreBody(bodyBytes: ByteArray, exchange: ServerWebExchange): ServerWebExchange {
//        val newRequestBody = Flux.just(exchange.response.bufferFactory().wrap(bodyBytes))
//
//        val decoratedRequest = object : ServerHttpRequestDecorator(exchange.request) {
//            override fun getBody(): Flux<DataBuffer> = newRequestBody
//        }
//
//        val mutatedExchange = exchange.mutate().request(decoratedRequest).build()
//        return mutatedExchange
//    }

    /*
    // Оборачиваем исходный запрос
                val decoratedRequest = object : ServerHttpRequestDecorator(request) {
                    override fun getBody(): Flux<DataBuffer> = newRequestBody
                }

                // Создаем новый exchange с новым запросом
                val decoratedExchange = exchange.mutate().request(decoratedRequest).build()

                return@flatMap chain.filter(decoratedExchange)
     */

//    private fun extractOperationName(query: String): String {
//        val document = Parser().parseDocument(query)
//        return document.definitions
//            .filterIsInstance<graphql.language.OperationDefinition>()
//            .flatMap { it.selectionSet.selections }
//            .filterIsInstance<graphql.language.Field>()
//            .first()
//            .name
//    }

}
