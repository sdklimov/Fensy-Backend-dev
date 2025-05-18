package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.User
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.repository.UserRepository

@FieldResolver
class PostFieldResolver(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) {

    @SchemaMapping(typeName = "Post", field = "author")
    suspend fun author(post: Post): User {
        return userRepository.findById(post.authorId)
    }

    @SchemaMapping(typeName = "User", field = "posts")
    suspend fun posts(user: User): List<Post> {
        return postRepository.findByAuthorId(user.id)
    }
}