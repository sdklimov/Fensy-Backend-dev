package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Country

@Component
class CountriesRepository(
    private val databaseClient: DatabaseClient
) {

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
            .map { Country(id = it["id"] as Long, code = it["code"] as String, name = null) }
            .awaitSingle()
    }

    suspend fun getByCode(countryCode: String): Country {
        return databaseClient
            .sql(
                """
                select id, code from countries where code = :code
            """.trimIndent()
            )
            .bind("code", countryCode)
            .fetch()
            .one()
            .map { Country(id = it["id"] as Long, code = it["code"] as String, name = null) }
            .awaitSingle()
    }

}
