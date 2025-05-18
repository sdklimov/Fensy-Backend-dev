package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Post
import ru.fensy.dev.repository.PostViewsRepository

@FieldResolver
class PostCountViewsFieldResolver(
    private val postViewsRepository: PostViewsRepository,
) {

    @SchemaMapping(typeName = "Post", field = "countViews")
    suspend fun countViews(post: Post): Long {
        return postViewsRepository.countPostViews(post.id)
    }

}
