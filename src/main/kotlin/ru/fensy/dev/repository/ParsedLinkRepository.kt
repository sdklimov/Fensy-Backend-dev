package ru.fensy.dev.repository

import java.math.BigDecimal
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.ParsedLink
import ru.fensy.dev.domain.ParsedLinkType

/**
 * Репозиторий parsed_links
 */
@Component
class ParsedLinkRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findByPostId(postId: Long): List<ParsedLink> =
        databaseClient
            .sql(
                """
                select * from parsed_links where post_id = :postId
            """.trimIndent()
            )
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    private fun of(source: Map<String, Any>) = source.let {
        ParsedLink(
            id = it["id"] as Long,
            postId = it["post_id"] as Long,
            type = ParsedLinkType.valueOf(it["type"] as String),
            link = it["link"] as String,
            picture = it["picture"] as? ByteArray,
            title = it["title"] as String,
            description = it["description"] as String,
            price = (it["price"] as BigDecimal).toDouble(),
            currency = it["currency"] as String,
        )
    }
}