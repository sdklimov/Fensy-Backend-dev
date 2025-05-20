package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Interest

/**
 * Репозиторий интересов
 */
@Component
class InterestsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findByPostId(postId: Long): List<Interest> =
        databaseClient
            .sql {
                """
                select * from interests where id in (select interest_id from post_interests where post_id = :postId);
            """.trimIndent()
            }
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    suspend fun findByParsedLinkId(parsedLinkId: Long): List<Interest> =
        databaseClient
            .sql {
                """
                select * from interests where id in (select interest_id from parsed_link_interests where parsed_link_id = :parsedLinkId);
            """.trimIndent()
            }
            .bind("parsedLinkId", parsedLinkId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    suspend fun addInterestsToPost(postId: Long, interests: List<Long>) {
        val values = interests.joinToString(", ") { "($postId, $it)" }
        databaseClient
            .sql("""
                insert into post_interests (post_id, interest_id) values $values
            """.trimIndent())
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun deleteInterestsFromPost(postId: Long, interestIds: List<Long>) {
        databaseClient
            .sql("""
                delete from post_interests where post_id = :postId and interest_id = any (:interestIds);
            """.trimIndent())
            .bind("postId", postId)
            .bind("interestIds", interestIds.toTypedArray())
            .fetch()
            .awaitRowsUpdated()
    }

    private fun of(source: Map<String, Any>) =
        source.let {
            Interest(
                id = it["id"] as Long,
                name = it["name"] as String,
            )
        }

}
