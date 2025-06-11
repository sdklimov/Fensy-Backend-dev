package ru.fensy.dev.webfilter

import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import ru.fensy.dev.constants.Constants.REQUEST_HTTP_HEADERS

@Component
class SetRequestHeadersToContextWebFilter(
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            exchange.request.headers
        }
            .flatMap {
                chain.filter(exchange)
                    .contextWrite { context ->
                        context.put(REQUEST_HTTP_HEADERS, it)
                    }
            }
            .then()
    }
}
