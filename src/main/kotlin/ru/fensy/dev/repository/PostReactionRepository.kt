package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.PostReaction

@Component
class PostReactionRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun checkReactionExists(postId: Long, emoji: String, userId: Long): PostReaction? {
        return databaseClient
            .sql(
                """
                select id, post_id, user_id, emoji from post_reactions
                where post_id = :postId and user_id = :userId and emoji = :emoji
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("userId", userId)
            .bind("emoji", emoji)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingleOrNull()
    }

    suspend fun getByPostId(postId: Long): List<PostReactionQueryRs> {
        return databaseClient
            .sql(
                """
                select emoji,
                       count(*) as count
                from post_reactions
                where post_id = :postId
                group by emoji
            """.trimIndent()
            )
            .bind("postId", postId)
            .fetch()
            .all()
            .map { PostReactionQueryRs(count = it["count"] as Long, emoji = it["emoji"] as String) }
            .collectList()
            .awaitSingle()
    }

    suspend fun deleteById(id: Long) =
        databaseClient
            .sql(
                """
                delete from post_reactions where id = :id
                """
            )
            .bind("id", id)
            .fetch()
            .awaitRowsUpdated()

    suspend fun create(reaction: PostReaction): Long {
        return databaseClient
            .sql(
                """
                insert into post_reactions(post_id, user_id, emoji)
                values (:postId, :userId, :emoji)
                returning id
            """.trimIndent()
            )
            .bind("postId", reaction.postId)
            .bind("userId", reaction.userId)
            .bind("emoji", reaction.emoji)
            .fetch()
            .one()
            .map { it["id"] as Long }
            .awaitSingle()
    }


    private fun of(source: Map<String, Any>): PostReaction {
        return source.let {
            PostReaction(
                id = it["id"] as Long,
                postId = it["post_id"] as Long,
                userId = it["user_id"] as Long,
                emoji = it["emoji"] as String
            )
        }
    }

}

data class PostReactionQueryRs(
    val count: Long,
    val emoji: String,
)