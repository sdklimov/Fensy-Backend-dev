package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
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

    suspend fun createOrUpdate(tags: List<String>): List<Long> {
        val values = tags.joinToString { "(\'$it\')" }
        return databaseClient
            .sql(
                """
                insert into tags(name) values $values on conflict(name) do update set name = EXCLUDED.name returning id;
            """.trimIndent()
            )
            .fetch()
            .all()
            .map { it["id"] as Long }
            .collectList()
            .awaitSingle()
    }

    suspend fun addTagsToPost(postId: Long, tagIds: List<Long>) {
        val values = tagIds.joinToString(", ") { "($postId, $it)" }
        databaseClient
            .sql(
                """
                insert into post_tags(post_id, tag_id) values $values
            """.trimIndent()
            )
            .fetch()
            .awaitRowsUpdated()
    }

    private fun of(source: Map<String, Any>) =
        source.let {
            Tag(
                id = it["id"] as Long,
                name = it["name"] as String,
            )
        }

}