package ru.fensy.dev.exceptionhandler

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import ru.fensy.dev.exception.UserNotExistsInContextException
import org.springframework.graphql.execution.ErrorType.UNAUTHORIZED
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST
import org.springframework.graphql.execution.ErrorType.INTERNAL_ERROR
import org.springframework.http.HttpStatus
import ru.fensy.dev.exception.ContentModerationException
import ru.fensy.dev.exception.UserCollectionAlreadyExistsException

@Component
class GraphQLExceptionResolver : DataFetcherExceptionResolverAdapter() {

    private val log = KotlinLogging.logger { }

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {

        log.error(ex) { "Непредвиденная ошибка"}

        return when (ex) {
            is UserNotExistsInContextException -> {
                GraphqlErrorBuilder.newError(env)
                    .message(ex.message ?: HttpStatus.UNAUTHORIZED.toString())
                    .errorType(UNAUTHORIZED)
                    .extensions(mapOf("code" to HttpStatus.UNAUTHORIZED.value()))
                    .build()
            }

            is IllegalArgumentException -> {
                GraphqlErrorBuilder.newError(env)
                    .message(ex.message ?: HttpStatus.BAD_REQUEST.toString())
                    .errorType(BAD_REQUEST)
                    .extensions(mapOf("code" to HttpStatus.BAD_REQUEST.value()))
                    .build()
            }

            is ContentModerationException -> {
                GraphqlErrorBuilder.newError(env)
                    .message(ex.message)
                    .errorType(BAD_REQUEST)
                    .extensions(mapOf("code" to HttpStatus.UNPROCESSABLE_ENTITY.value()))
                    .build()
            }

            is UserCollectionAlreadyExistsException -> {
                GraphqlErrorBuilder.newError(env)
                    .message(ex.message)
                    .errorType(BAD_REQUEST)
                    .extensions(mapOf("code" to HttpStatus.BAD_REQUEST.value()))
                    .build()
            }

            else -> {
                GraphqlErrorBuilder.newError(env)
                    .message(ex.message ?: "Что-то пошло не так")
                    .errorType(INTERNAL_ERROR)
                    .extensions(mapOf("code" to HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .build()
            }
        }
    }
}
