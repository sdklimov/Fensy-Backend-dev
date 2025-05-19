package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Collection

/**
 * Репозиторий collections
 */
@Component
class CollectionRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findByPostId(postId: Long): List<Collection> =
        databaseClient
            .sql(
                """
                select * from collections where id in (select collection_id from collection_posts where post_id = :postId);
            """.trimIndent()
            )
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    suspend fun findAttachedCollectionsByPostId(postId: Long): List<Collection> =
        databaseClient
            .sql(
                """
                select * from collections where id in (select collection_id from post_collections where post_id = :postId);
            """.trimIndent()
            )
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    private fun of(source: Map<String, Any>) = source.let {
        Collection(
            id = it["id"] as Long,
            title = it["title"] as String,
            description = it["description"] as String,
            allowViewingFor = it["allow_viewing_for"] as String,
        )
    }


}
