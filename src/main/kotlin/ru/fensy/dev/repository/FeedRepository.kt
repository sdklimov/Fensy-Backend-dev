package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.domain.Post

@Repository
class FeedRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun getForUser(userId: Long, pageRequest: PageRequest): List<Post> {
        return databaseClient
            .sql(
                """
        WITH user_interest_weights AS (SELECT interest_id,
                                              weight
                                       FROM user_interests
                                       WHERE user_id = :userId),

             posts_with_weights AS (SELECT   p.id,
                                             p.original_post_id,
                                             p.is_repost,
                                             p.author_id,
                                             p.allow_viewing_for,
                                             p.pinned,
                                             p.adult_content,
                                             p.is_deleted,
                                             p.title,
                                             p.content,
                                             p.created_at,
                                             p.updated_at,
                                             pi.interest_id,
                                             COALESCE(uiw.weight, 0)                                                      AS interest_weight,
                                           -- Добавляем ранжирование для стабильной пагинации
                                           ROW_NUMBER()
                                           OVER (ORDER BY COALESCE(uiw.weight, 0) DESC, p.created_at DESC)              AS global_rank
                                    FROM posts p
                                             LEFT JOIN post_interests pi ON p.id = pi.post_id
                                             LEFT JOIN user_interest_weights uiw ON pi.interest_id = uiw.interest_id
                                             where not p.is_deleted)

        SELECT *
        FROM posts_with_weights
        ORDER BY global_rank
        LIMIT :limit OFFSET :offset;
    """.trimIndent()
            )
            .bind("limit", pageRequest.pageSize)
            .bind("offset", pageRequest.offset)
            .bind("userId", userId)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingleOrNull() ?: emptyList()
    }

    suspend fun getForAll(pageRequest: PageRequest): List<Post> {
        return emptyList()
    }


}
