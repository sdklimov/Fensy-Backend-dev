package ru.fensy.dev.usecase.post

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.file.FilePersister
import ru.fensy.dev.graphql.controller.post.input.CreatePostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.repository.CollectionRepository
import ru.fensy.dev.repository.InterestsRepository
import ru.fensy.dev.repository.ParsedLinkRepository
import ru.fensy.dev.repository.PostAttachmentRepository
import ru.fensy.dev.repository.PostRepository
import ru.fensy.dev.repository.TagsRepository
import ru.fensy.dev.repository.querydata.CreateParsedLinkQueryData
import ru.fensy.dev.repository.querydata.CreatePostQueryData
import ru.fensy.dev.service.FileMimeTypeValidateService
//import ru.fensy.dev.service.ValidateCreatePostRequestService

/**
 * Создать пост
 */
@Component
@Transactional
class CreatePostUseCase(
    private val postRepository: PostRepository,
    private val fileMimeTypeValidateService: FileMimeTypeValidateService,
//    private val validateCreatePostRequestService: ValidateCreatePostRequestService,
    private val interestRepository: InterestsRepository,
    private val tagsRepository: TagsRepository,
    private val parsedLinkRepository: ParsedLinkRepository,
    private val collectionRepository: CollectionRepository,
    private val filePersister: FilePersister,
    private val postAttachmentRepository: PostAttachmentRepository,
) {

    suspend fun execute(input: CreatePostInput): PostResponse = coroutineScope {
        val currentUserId = 1L // todo: Брать из контекста когда будет JWT
//        input.attachments?.let {
//            fileMimeTypeValidateService.validate(it)
//        }
//            ?.takeIf { it.isNotSuccessful() }
//            ?.let {
//                PostResponse(
//                    post = null,
//                    message = it.message ?: "Ошибка валидации типов файлов",
//                    success = false
//                )
//            }
//
//        validateCreatePostRequestService.validate(input)
//            .takeIf { it.isNotEmpty() }
//            ?.let { return@coroutineScope PostResponse(post = null, message = it.joinToString(", "), success = false) }

        if (input.pinned) {
            postRepository.resetPinned(currentUserId)
        }

        val newPostRq = CreatePostQueryData(
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
//            async { processAttachments(createdPost.id, input) }
        )

        deferred.awaitAll()

        return@coroutineScope PostResponse(post = createdPost, success = true, message = "Пост успешно создан")

    }

//    private suspend fun processAttachments(postId: Long, input: CreatePostInput) {
//        input.attachments
//            ?.map { attachment ->
//                val path = filePersister.save(attachment.content(), attachment.filename(), postId)
//                postAttachmentRepository.savePostAttachment(postId, path)
//            }
//    }

    private suspend fun createInterests(postId: Long, input: CreatePostInput) {
        input.interestIds.takeIf { it.isNotEmpty() }
            ?.let { interestIds ->
                interestRepository.addInterestsToPost(postId, interestIds)
            }
    }

    private suspend fun createTags(postId: Long, input: CreatePostInput) {
        input.tags.takeIf { it.isNotEmpty() }
            ?.let {
                val tagIds = tagsRepository.createOrUpdate(it)
                tagsRepository.addTagsToPost(postId, tagIds)
            }
    }

    private suspend fun processParsedLinks(postId: Long, input: CreatePostInput) {
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

    private suspend fun processCollections(postId: Long, input: CreatePostInput) {
        input.collectionIds
            .takeIf { it.isNotEmpty() }
            ?.let { collectionIds ->
                collectionRepository.addPostToCollections(postId, collectionIds)
            }
    }

}
