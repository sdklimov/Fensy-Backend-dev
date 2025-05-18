package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.Tag
import ru.fensy.dev.repository.TagsRepository

@FieldResolver
class PostTagsFieldResolver(
    private val tagsRepository: TagsRepository,
) {

    @SchemaMapping(typeName = "Post", field = "tags")
    suspend fun tags(post: Post): List<Tag> {
        return tagsRepository.getTagsByPostId(post.id)
    }

}
