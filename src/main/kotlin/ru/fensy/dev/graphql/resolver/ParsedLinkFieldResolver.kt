package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Interest
import ru.fensy.dev.domain.ParsedLink
import ru.fensy.dev.repository.InterestsRepository

@FieldResolver
class ParsedLinkFieldResolver(
    private val interestsRepository: InterestsRepository,

) {

    @SchemaMapping(typeName = "ParsedLink", field = "interests")
    suspend fun author(parsedLink: ParsedLink): List<Interest> {
        return interestsRepository.findByParsedLinkId(parsedLink.id)
    }

}
