package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Tag

/**
 * Репозиторий таблицы tags
 */
@Component
class TagsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun getTagsByPostId(postId: Long): List<Tag> =
        databaseClient
            .sql {
                """
                select * from tags where id in (select tag_id from post_tags where post_id = :postId)"""
            }
            .bind("postId", postId)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()

    private fun of(source: Map<String, Any>) =
        source.let {
            Tag(
                id = it["id"] as Long,
                name = it["name"] as String,
            )
        }

}