package ru.fensy.dev.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Component
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.domain.Post
import ru.fensy.dev.repository.querydata.CreatePostQueryData
import ru.fensy.dev.repository.querydata.UpdatePostQueryData

@Component
class PostRepository(
    private val databaseClient: DatabaseClient,
) {

    suspend fun findById(id: Long): Post {
        return databaseClient
            .sql {
                """
                select * from posts where id = :id and not is_deleted
            """.trimIndent()
            }
            .bind("id", id)
            .fetch()
            .one()
            .map { Post.of(it) }
            .awaitSingle()
    }

    suspend fun findByAuthorId(id: Long): List<Post> {
        return databaseClient
            .sql {
                """
                select * from posts where author_id = :authorId
                and not is_deleted
            """.trimIndent()
            }
            .bind("authorId", id)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun findByUserName(userName: String, pageRequest: PageRequest): List<Post> {
        return databaseClient
            .sql {
                """
                    select * from posts p
                    join users u on p.author_id = u.id
                    where u.username = :userName
                    and not p.is_deleted
                    and p.allow_viewing_for != 'NONE'
                    offset :offset limit :limit
            """.trimIndent()
            }
            .bind("userName", userName)
            .bind("offset", pageRequest.offset)
            .bind("limit", pageRequest.pageSize)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingle()
    }

    suspend fun getByCollectionId(collectionId: Long): List<Post> =
        databaseClient
            .sql(
                """
                select * from posts where id = any ( select post_id from collection_posts where collection_id = :collectionId) and not is_deleted
            """.trimIndent()
            )
            .bind("collectionId", collectionId)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingle()

    suspend fun findByOriginalPostId(originalPostId: Long): List<Post> =
        databaseClient
            .sql(
                """
                select * from posts where original_post_id = :originalPostId and not is_deleted
            """.trimIndent()
            )
            .bind("originalPostId", originalPostId)
            .fetch()
            .all()
            .map { Post.of(it) }
            .collectList()
            .awaitSingle()

    suspend fun resetPinned(authorId: Long) {
        databaseClient
            .sql("update posts set pinned = false where author_id = :authorId and not is_deleted")
            .bind("authorId", authorId)
            .fetch()
            .awaitRowsUpdated()
    }

    suspend fun create(post: CreatePostQueryData): Post =
        databaseClient
            .sql(
                """
              insert into posts(author_id, title, content, allow_viewing_for, original_post_id)
                values (:authorId, :title, :content, :allowViewingFor, :originalPostId)
                returning *; 
            """.trimIndent()
            )
            .bind("authorId", post.authorId)
            .bind("title", post.title)
            .bind("content", post.content)
            .bind("allowViewingFor", post.allowViewingFor.name)
            .bind("originalPostId", post.originalPostId)
            .fetch()
            .one()
            .map { Post.of(it) }
            .awaitSingle()

    suspend fun update(post: UpdatePostQueryData): Post {
        return databaseClient
            .sql(
                """
                update posts set author_id = :authorId,  title = :title, content = :content, allow_viewing_for = :allowViewingFor
                where id = :postId and not is_deleted
                returning *; 
            """.trimIndent()
            )
            .bind("postId", post.id)
            .bind("authorId", post.authorId)
            .bind("title", post.title)
            .bind("content", post.content)
            .bind("allowViewingFor", post.allowViewingFor.name)
            .fetch()
            .one()
            .map { Post.of(it) }
            .awaitSingle()
    }


}