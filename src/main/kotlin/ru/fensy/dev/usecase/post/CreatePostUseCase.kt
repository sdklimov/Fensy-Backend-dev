package ru.fensy.dev.usecase.post

import java.util.UUID
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.constants.Constants.CONTENT_MODERATION_EXCEPTION
import ru.fensy.dev.exception.FileUploadSessionNotExistsException
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.proxy.OpenAIModerationProxyService
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.repository.FileUploadSessionRepository
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.repository.ParsedLinkRepository
import ru.fensy.dev.repository.PostAttachmentRepository
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.repository.TagsRepository
import ru.fensy.dev.repository.querydata.CreateParsedLinkQueryData
import ru.fensy.dev.repository.querydata.CreatePostQueryData
import ru.fensy.dev.usecase.BaseUseCase
import ru.fensy.dev.usecase.post.operationmodel.CreatePostOperationRq

/**
 * Создать пост
 */
@Component
@Transactional
class CreatePostUseCase(
    private val postRepository: PostRepository,
    private val interestRepository: InterestsRepository,
    private val tagsRepository: TagsRepository,
    private val parsedLinkRepository: ParsedLinkRepository,
    private val collectionRepository: CollectionRepository,
    private val postAttachmentRepository: PostAttachmentRepository,
    private val fileUploadSessionRepository: FileUploadSessionRepository,
    private val openAIModerationProxyService: OpenAIModerationProxyService,
) : BaseUseCase() {

    suspend fun execute(input: CreatePostOperationRq): PostResponse = coroutineScope {

        val currentUserId = currentUser(true)!!.id!!

        if (input.pinned) {
            postRepository.resetPinned(currentUserId)
        }

        val moderationResult = openAIModerationProxyService.moderate(content = input.content)

        when (moderationResult) {
            FLAGGED -> throw CONTENT_MODERATION_EXCEPTION
            "OK" -> {}
            else -> {throw IllegalArgumentException("Ошибка при выполнении валидации")}
        }

        val newPostRq = CreatePostQueryData(
            originalPostId = input.originalPostId,
            authorId = currentUserId,
            title = input.title,
            content = input.content,
            allowViewingFor = input.allowViewingFor,
            pinned = input.pinned,
        )

        val createdPost = postRepository.create(newPostRq)

        val deferred: List<Deferred<Unit>> = listOf(
            async { createInterests(createdPost.id, input) },
            async { createTags(createdPost.id, input) },
            async { processParsedLinks(createdPost.id, input) },
            async { processCollections(createdPost.id, input) },
            async {
                input.fileSessionId?.let {
                    processAttachments(createdPost.id, it, currentUserId)
                } ?: Unit
            }
        )

        deferred.awaitAll()

        return@coroutineScope PostResponse(post = createdPost, success = true, message = "Пост успешно создан")

    }

    private suspend fun createInterests(postId: Long, input: CreatePostOperationRq) {
        input.interestIds.takeIf { it.isNotEmpty() }
            ?.let { interestIds ->
                interestRepository.addInterestsToPost(postId, interestIds)
            }
    }

    private suspend fun createTags(postId: Long, input: CreatePostOperationRq) {
        input.tags.takeIf { it.isNotEmpty() }
            ?.let {
                val tagIds = tagsRepository.createOrUpdate(it)
                tagsRepository.addTagsToPost(postId, tagIds)
            }
    }

    private suspend fun processParsedLinks(postId: Long, input: CreatePostOperationRq) {
        input.parsedLinks?.takeIf { it.isNotEmpty() }
            ?.map { parsedLink ->
                val rq = CreateParsedLinkQueryData(
                    postId = postId,
                    type = parsedLink.type,
                    link = parsedLink.link,
                    picture = parsedLink.picture,
                    title = parsedLink.title,
                    description = parsedLink.description,
                    price = parsedLink.price,
                    currency = parsedLink.currency
                )
                val parsedLinkId = parsedLinkRepository.create(rq).id
                parsedLinkRepository.addParsedLinkToInterest(parsedLinkId, parsedLinkId)
            }

    }

    private suspend fun processAttachments(
        createdPostId: Long,
        fileSessionId: UUID,
        userId: Long,
    ) {
        val session = fileUploadSessionRepository.getActiveSessionByIdAndUserId(fileSessionId, userId)
            ?: throw FileUploadSessionNotExistsException("Сессия не обнаружена")

        val files = fileUploadSessionRepository.getSessionFiles(session.id)
        files.takeIf {
            it.isNotEmpty()
        }
            ?.let { fileIds ->
                postAttachmentRepository.savePostAttachments(createdPostId, fileIds)
            }
        fileUploadSessionRepository.closeSession(sessionId = session.id)
    }

    private suspend fun processCollections(postId: Long, input: CreatePostOperationRq) {
        input.collectionIds
            .takeIf { it.isNotEmpty() }
            ?.let { collectionIds ->
                collectionRepository.addPostToCollections(postId, collectionIds)
            }
    }

    companion object {
        private const val FLAGGED =  "Flagged"
    }

}
