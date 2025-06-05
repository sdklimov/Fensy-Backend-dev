package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.Comment
import ru.fensy.dev.domain.PageRequest

@Component
class CommentsRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun create(content: String, authorId: Long, postId: Long, parentId: Long?): Comment {
        return databaseClient
            .sql(
                """
                insert into comments(content, author_id, post_id, parent_id) values (
                :content, :author_id, :post_id, :parent_id
                ) returning *
            """.trimIndent()
            )
            .bind("content", content)
            .bind("author_id", authorId)
            .bind("post_id", postId)
            .bind("parent_id", parentId)
            .fetch()
            .one()
            .map { of(it) }
            .awaitSingle()
    }

    suspend fun getByPostId(postId: Long, pageRequest: PageRequest): List<Comment> {
        return databaseClient
            .sql(
                """
                    select *, (select exists(select 1 from comments cc where cc.parent_id = c.id limit 1)) as has_children
                    from comments c
                    where post_id = :postId
                      and parent_id is null
                offset :offset limit :limit
            """.trimIndent()
            )
            .bind("postId", postId)
            .bind("offset", pageRequest.offset)
            .bind("limit", pageRequest.pageSize)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun getChildren(commentId: Long, pageRequest: PageRequest): List<Comment> {
        return databaseClient
            .sql(
                """
                    select *, (select exists(select 1 from comments cc where cc.parent_id = c.id limit 1)) as has_children
                    from comments c
                    where parent_id = :commentId
                offset :offset limit :limit
            """.trimIndent()
            )
            .bind("commentId", commentId)
            .bind("offset", pageRequest.offset)
            .bind("limit", pageRequest.pageSize)
            .fetch()
            .all()
            .map { of(it) }
            .collectList()
            .awaitSingle()
    }

    private fun of(source: Map<String, Any>) = source.let {
        Comment(
            id = it["id"] as Long,
            content = it["content"] as String,
            authorId = it["author_id"] as Long,
            postId = it["post_id"] as Long,
            parentId = it["parent_id"] as? Long,
            hasChildren = it["has_children"] as Boolean,
        )
    }

}
