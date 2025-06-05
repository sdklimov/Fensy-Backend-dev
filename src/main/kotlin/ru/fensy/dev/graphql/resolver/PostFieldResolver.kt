package ru.fensy.dev.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Collection
import ru.fensy.dev.domain.Comment
import ru.fensy.dev.domain.Interest
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.domain.ParsedLink
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.PostAttachment
import ru.fensy.dev.domain.Tag
import ru.fensy.dev.domain.User
import ru.fensy.dev.graphql.controller.post.response.CommentResponse
import ru.fensy.dev.properties.PostProperties
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.repository.CommentsRepository
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.repository.ParsedLinkRepository
import ru.fensy.dev.repository.PostAttachmentsRepository
import ru.fensy.dev.repository.PostLikeRepository
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
    private val collectionRepository: CollectionRepository,
    private val postAttachmentsRepository: PostAttachmentsRepository,
    private val postLikeRepository: PostLikeRepository,
    private val commentsRepository: CommentsRepository,
    private val postProperties: PostProperties,
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

    @SchemaMapping(typeName = "Post", field = "collections")
    suspend fun collections(post: Post): List<Collection> {
        return collectionRepository.findByPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "attachments")
    suspend fun attachments(post: Post): List<PostAttachment> {
        return postAttachmentsRepository.findByPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "reposts")
    suspend fun reposts(post: Post): List<Post> {
        return postRepository.findByOriginalPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "attachedCollections")
    suspend fun attachedCollections(post: Post): List<Collection> {
        return collectionRepository.findAttachedCollectionsByPostId(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "likes")
    suspend fun likes(post: Post): Long {
        return postLikeRepository.getPostLikes(post.id)
    }

    @SchemaMapping(typeName = "Post", field = "comments")
    suspend fun comments(env: DataFetchingEnvironment): List<Comment> {
        val pageNumber = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageNumber")?.getValue<Int>() ?: 1
        val pageSize = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageSize")?.getValue<Int>() ?: postProperties.pagination.pageSize

        val post = env.getSource<Post>()
        return commentsRepository.getByPostId(post!!.id, PageRequest(pageNumber, pageSize))
    }

}
