package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Language

@Component
class LanguagesRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun getAll(): List<Language> {
        return databaseClient
            .sql(
                """
                select * from languages
            """.trimIndent()
            )
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun getById(id: Long): Language {
        return databaseClient
            .sql(
                """
                select id, code, name from languages where id = :id
            """.trimIndent()
            )
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

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
            .map { of(it) }
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>): Language {
        return source.let {
            Language(id = it["id"] as Long, code = it["code"] as String, name = it["name"] as String)
        }
    }

}
