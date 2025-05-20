package ru.fensy.dev.repository

import java.math.BigDecimal
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.ParsedLink
import ru.fensy.dev.domain.ParsedLinkType
import ru.fensy.dev.repository.querydata.CreateParsedLinkQueryData

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

    suspend fun create(parsedLink: CreateParsedLinkQueryData): ParsedLink {
        return databaseClient
            .sql(
                """
                insert into parsed_links(post_id, type, link, picture, title, description, price, currency) 
                values (:postId, :type, :link, :picture, :title, :description, :price, :currency) returning *
            """.trimIndent()
            )
            .bind("postId", parsedLink.postId)
            .bind("type", parsedLink.type.name)
            .bind("link", parsedLink.link)
            .bind("picture", parsedLink.picture)
            .bind("title", parsedLink.title)
            .bind("description", parsedLink.description)
            .bind("price", parsedLink.price)
            .bind("currency", parsedLink.currency)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun deleteById(ids: List<Long>) {
        databaseClient
            .sql("""
                delete from parsed_links where id = any (:ids)
            """.trimIndent())
            .bind("ids", ids.toTypedArray())
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun addParsedLinkToInterest(parsedLinkId: Long, interestId: Long) {
        databaseClient
            .sql("""
                insert into parsed_link_interests (parsed_link_id, interest_id) values (:parsedLinkId, :interestId)
            """.trimIndent())
            .bind("parsedLinkId", parsedLinkId)
            .bind("interestId", interestId)
            .fetch()
            .awaitRowsUpdated()
    }

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