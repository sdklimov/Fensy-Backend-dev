package ru.fensy.dev.configuration.scalar

import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import java.io.File

object UploadScalar {

    val Upload: GraphQLScalarType = GraphQLScalarType.newScalar()
        .name("Upload")
        .description("A file part in a multipart request.")
        .coercing(object : Coercing<File, Void?> {
            override fun parseValue(input: Any): File {
                throw CoercingParseValueException("Not implemented. Use REST endpoint.")
            }

            override fun parseLiteral(input: Any): File {
                throw CoercingParseLiteralException("Upload scalar does not support literal values.")
            }

            override fun serialize(dataFetcherResult: Any): Void? {
                return null
            }
        })
        .build()
}
