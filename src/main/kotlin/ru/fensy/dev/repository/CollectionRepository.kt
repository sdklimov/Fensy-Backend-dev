package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.AllowViewingFor
import ru.fensy.dev.domain.Collection

/**
 * Репозиторий collections
 */
@Component
class CollectionRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findById(id: Long): Collection? =
        databaseClient
            .sql(
                """
                select * from collections where id = :id;
            """.trimIndent()
            )
            .bind("id", id)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()

    suspend fun checkCollectionExistsWithTitle(title: String, userId: Long): Boolean {
        return databaseClient
            .sql(
                """
                select exists((select 1 from collections where title = :title and author_id = :userId))
            """.trimIndent()
            )
            .bind("title", title)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { it["exists"] as Boolean }
            .awaitSingle()
    }

    suspend fun createUserCollection(rq: CreateUserCollectionQueryRq): Collection =
        databaseClient
            .sql(
                """
                insert into collections(author_id, title, description, allow_viewing_for) 
                values (:userId, :title, :description, :allowViewingFor)
                returning *
            """.trimIndent()
            )
            .bind("userId", rq.userId)
            .bind("title", rq.title)
            .bind("description", rq.description)
            .bind("allowViewingFor", rq.allowViewingFor.name)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()

    suspend fun findByUserId(userId: Long): List<Collection> =
        databaseClient
            .sql(
                """
                select * from collections where author_id = :userId;
            """.trimIndent()
            )
            .bind("userId", userId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

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

    suspend fun addPostToCollections(postId: Long, collectionIds: List<Long>) {
        val values = collectionIds.joinToString(", ") { "($postId, $it)" }
        databaseClient
            .sql(
                """
                insert into post_collections(post_id, collection_id) values $values;
            """.trimIndent()
            )
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun deleteFromPost(postId: Long, ids: List<Long>) {
        databaseClient
            .sql(
                """
                delete from post_collections where post_id = :postId and collection_id = any (:ids)
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("ids", ids.toTypedArray())
            .fetch()
            .awaitRowsUpdated()
    }

    private fun of(source: Map<String, Any>) = source.let {
        Collection(
            id = it["id"] as Long,
            title = it["title"] as String,
            description = it["description"] as String,
            allowViewingFor = it["allow_viewing_for"] as String,
        )
    }


}


data class CreateUserCollectionQueryRq(
    val userId: Long,
    val title: String,
    val description: String,
    val allowViewingFor: AllowViewingFor,
)