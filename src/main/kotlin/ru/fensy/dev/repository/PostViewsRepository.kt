package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

/**
 * Репозиторий таблицы post_views
 */
@Component
class PostViewsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun countPostViews(postId: Long): Long =
        databaseClient
            .sql {
                """
                select count(*) from post_views where post_id = :postId
            """.trimIndent()
            }
            .bind("postId", postId)
            .fetch()
            .one()
            .map { it["count"] as Long }
            .awaitSingle()

    suspend fun addPostView(postId: Long, idAddress: String) {
        databaseClient
            .sql("""
                insert into post_views (post_id, ip_address)
                values (:postId, :ipAddress::inet)
                on conflict do nothing ;
            """.trimIndent())
            .bind("postId", postId)
            .bind("ipAddress", idAddress)
            .fetch()
            .awaitRowsUpdated()
    }

}