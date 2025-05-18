package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.Tag
import ru.fensy.dev.domain.User
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.repository.PostViewsRepository
import ru.fensy.dev.repository.TagsRepository
import ru.fensy.dev.repository.UserRepository

@FieldResolver
class PostFieldResolver(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val postViewsRepository: PostViewsRepository,
    private val tagsRepository: TagsRepository,
) {

    @SchemaMapping(typeName = "Post", field = "author")
    suspend fun author(post: Post): User {
        return userRepository.findById(post.authorId)
    }

    @SchemaMapping(typeName = "Post", field = "originalPost")
    suspend fun originalPost(post: Post): Post? =
        post.originalPostId?.let {
            return postRepository.findById(it)
        }

    @SchemaMapping(typeName = "User", field = "posts")
    suspend fun posts(user: User): List<Post> {
        return postRepository.findByAuthorId(user.id)
    }

    @SchemaMapping(typeName = "Post", field = "countViews")
    suspend fun countViews(post: Post): Long {
        return postViewsRepository.countPostViews(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "tags")
    suspend fun tags(post: Post): List<Tag> {
        return tagsRepository.getTagsByPostId(post.id)
    }

}
