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
        with user_interest_weights as (select interest_id,
                                              weight
                                       from user_interests
                                       where user_id = :userId),

             posts_with_weights as (select   p.id,
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
                                             coalesce(uiw.weight, 0)                                                      as interest_weight,
                                           -- добавляем ранжирование для стабильной пагинации
                                           row_number()
                                           over (order by coalesce(uiw.weight, 0) desc, p.created_at desc)              as global_rank
                                    from posts p
                                             left join post_interests pi on p.id = pi.post_id
                                             left join user_interest_weights uiw on pi.interest_id = uiw.interest_id
                                             where not p.is_deleted and p.allow_viewing_for != 'NONE')

        select *
        from posts_with_weights
        order by global_rank
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
                from post_likes
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
