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
                select *
                from posts
                where not is_deleted
                  and allow_viewing_for != 'NONE'
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
