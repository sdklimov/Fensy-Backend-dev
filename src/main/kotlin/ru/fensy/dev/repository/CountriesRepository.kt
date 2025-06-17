package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Country

@Component
class CountriesRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findAll(): List<Country> {
        return databaseClient
            .sql(
                """
                select * from countries
            """.trimIndent()
            )
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun getById(id: Long): Country {
        return databaseClient
            .sql(
                """
                select * from countries where id = :id
            """.trimIndent()
            )
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun getByCode(countryCode: String): Country {
        return databaseClient
            .sql(
                """
                select * from countries where code = :code
            """.trimIndent()
            )
            .bind("code", countryCode)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>): Country {
      return  source.let {
            Country(id = it["id"] as Long, code = it["code"] as String, name = it["name"] as String)
        }
    }

}
