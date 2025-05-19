package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Collection
import ru.fensy.dev.domain.CollectionCover
import ru.fensy.dev.domain.Post
import ru.fensy.dev.repository.CollectionCoversRepository
import ru.fensy.dev.repository.PostRepository

@FieldResolver
class CollectionFieldResolver(
    private val collectionCoversRepository: CollectionCoversRepository,
    private val postRepository: PostRepository,
) {

    @SchemaMapping(typeName = "Collection", field = "covers")
    suspend fun covers(collection: Collection): List<CollectionCover> {
        return collectionCoversRepository.getByCollectionId(collection.id)
    }

    @SchemaMapping(typeName = "Collection", field = "posts")
    suspend fun posts(collection: Collection): List<Post> {
        return postRepository.getByCollectionId(collection.id)
    }

}
