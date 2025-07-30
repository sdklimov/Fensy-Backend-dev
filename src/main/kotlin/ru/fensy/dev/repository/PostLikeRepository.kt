package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component

@Component
class PostLikeRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun checkLikeExists(postId: Long, userId: Long): Boolean {
        return databaseClient
            .sql(
                """
                select exists(select 1 from post_reactions where post_id = :postId and user_id = :userId)
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("userId", userId)
            .fetch()
            .one()
            .map { it["exists"] as Boolean }
            .awaitSingle()

    }

    suspend fun addOrDeletePostLike(postId: Long, userId: Long) {
        databaseClient
            .sql(
                """
              insert into post_reactions(post_id, user_id) values (:postId, :userId);
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("userId", userId)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun delete(postId: Long, userId: Long) {
        databaseClient
            .sql(
                """
                delete from post_reactions where post_id = :postId and user_id = :userId
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("userId", userId)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun getPostLikes(postId: Long): Long {
        return databaseClient.sql(
            """
            select count(*) from post_reactions where post_id = :postId
        """.trimIndent()
        )
            .bind("postId", postId)
            .fetch()
            .one()
            .map { it["count"] as Long }
            .awaitSingle()
    }

}