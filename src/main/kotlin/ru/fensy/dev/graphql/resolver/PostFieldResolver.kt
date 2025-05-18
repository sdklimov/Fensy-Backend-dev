package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Interest
import ru.fensy.dev.domain.ParsedLink
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.Tag
import ru.fensy.dev.domain.User
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.repository.ParsedLinkRepository
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
    private val interestsRepository: InterestsRepository,
    private val parsedLinkRepository: ParsedLinkRepository,
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

    @SchemaMapping(typeName = "Post", field = "countViews")
    suspend fun countViews(post: Post): Long {
        return postViewsRepository.countPostViews(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "tags")
    suspend fun tags(post: Post): List<Tag> {
        return tagsRepository.getTagsByPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "interests")
    suspend fun interests(post: Post): List<Interest> {
        return interestsRepository.findByPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "parsedLinks")
    suspend fun parsedLinks(post: Post): List<ParsedLink> {
        return parsedLinkRepository.findByPostId(post.id)
    }

}
