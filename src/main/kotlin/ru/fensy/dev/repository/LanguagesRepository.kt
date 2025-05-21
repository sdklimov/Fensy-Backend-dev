package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Language

@Component
class LanguagesRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun getByCode(languageCode: String): Language {
        return databaseClient
            .sql(
                """
                select id, code, name from languages where code = :code
            """.trimIndent()
            )
            .bind("code", languageCode)
            .fetch()
            .one()
            .map { Language(id = it["id"] as Long, code = it["code"] as String, name = it["name"] as String) }
            .awaitSingle()
    }

}
