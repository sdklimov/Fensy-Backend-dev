package ru.fensy.dev.exceptionhandler

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import ru.fensy.dev.exception.UserNotExistsInContextException
import org.springframework.graphql.execution.ErrorType.UNAUTHORIZED
import org.springframework.graphql.execution.ErrorType.BAD_REQUEST
import org.springframework.graphql.execution.ErrorType.INTERNAL_ERROR
import org.springframework.http.HttpStatus

@Component
class GraphQLExceptionResolver : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {
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
