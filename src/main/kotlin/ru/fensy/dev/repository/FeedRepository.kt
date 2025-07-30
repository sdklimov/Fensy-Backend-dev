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
                    SELECT DISTINCT ON (p.id) p.*, COALESCE(SUM(ui.weight) OVER (PARTITION BY p.id), 0) AS relevance
            FROM posts p
                     JOIN post_interests pi ON p.id = pi.post_id
                     JOIN user_interests ui ON pi.interest_id = ui.interest_id AND ui.user_id = :userId
            WHERE p.is_deleted = false
              AND (p.allow_viewing_for = 'ANY')
            ORDER BY p.id, p.pinned DESC, relevance DESC, p.created_at DESC
                    limit :limit offset :offset;
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
        return databaseClient
            .sql(
                """
                   with like_counts as (
                select post_id, count(*) as like_count
                from post_reactions
                group by post_id
            ),
                 comment_counts as (
                     select post_id, count(*) as comment_count
                     from comments
                     group by post_id
                 ),
                 popularity_ranked as (
                     select
                         p.*,
                         coalesce(lc.like_count, 0) + coalesce(cc.comment_count, 0) as popularity,
                         row_number() over (order by coalesce(lc.like_count, 0) + coalesce(cc.comment_count, 0) desc, p.created_at desc) as pop_rank
                     from posts p
                              left join like_counts lc on lc.post_id = p.id
                              left join comment_counts cc on cc.post_id = p.id
                     where not p.is_deleted
                       and p.allow_viewing_for != 'NONE'
                 ),
                 popular_posts as (
                     select * from popularity_ranked
                     where pop_rank <= (SELECT ceil(:limit * 0.8))
                 ),
                 newest_ranked as (
                     select
                         p.*,
                         0 as popularity,
                         row_number() over (order by p.created_at desc) as new_rank
                     from posts p
                     where not p.is_deleted
                       and p.allow_viewing_for != 'NONE'
                       and p.id not in (select id from popular_posts) -- 💥 исключаем популярные
                 ),
                 new_posts as (
                     select * from newest_ranked
                     where new_rank <= (SELECT ceil(:limit * 0.2))
                 ),
                 combined as (
                     select * from popular_posts
                     union all
                     select * from new_posts
                 )
            select *
            from combined
            order by created_at desc
            limit :limit offset :offset;
            """.trimIndent()
            )
            .bind("limit", pageRequest.pageSize)
            .bind("offset", pageRequest.offset)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingleOrNull() ?: emptyList()
    }


}
