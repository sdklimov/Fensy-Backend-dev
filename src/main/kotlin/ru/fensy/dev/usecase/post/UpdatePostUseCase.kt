package ru.fensy.dev.usecase.post

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.Tag
import ru.fensy.dev.exception.PostNotFoundException
import ru.fensy.dev.graphql.controller.post.input.UpdatePostInput
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.repository.*
import ru.fensy.dev.repository.querydata.CreateParsedLinkQueryData
import ru.fensy.dev.repository.querydata.UpdatePostQueryData
import ru.fensy.dev.service.FileMimeTypeValidateService

//import ru.fensy.dev.service.ValidateCreatePostRequestService

/**
 * Создать пост
 */
@Component
@Transactional
class UpdatePostUseCase(
    private val postRepository: PostRepository,
    private val fileMimeTypeValidateService: FileMimeTypeValidateService,
//    private val validateCreatePostRequestService: ValidateCreatePostRequestService,
    private val interestRepository: InterestsRepository,
    private val tagsRepository: TagsRepository,
    private val parsedLinkRepository: ParsedLinkRepository,
    private val collectionRepository: CollectionRepository,
    private val postAttachmentRepository: PostAttachmentRepository,
) {

    suspend fun execute(input: UpdatePostInput): PostResponse = coroutineScope {
        val currentUserId = 1L // todo: Брать из контекста когда будет JWT
        val currentPost = postRepository.findById(input.id) ?: throw PostNotFoundException()
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

        val updatePostRq = UpdatePostQueryData(
            id = input.id,
            authorId = currentUserId,
            title = input.title,
            content = input.content,
            allowViewingFor = input.allowViewingFor,
            pinned = input.pinned,
        )

        val updatedPost = postRepository.update(updatePostRq)

        val deferred: List<Deferred<Unit?>> = listOf(
            async { updateInterests(currentPost, input) },
            async { updateTags(currentPost, input) },
            async { updateParsedLinks(currentPost, input) },
            async { processCollections(currentPost, input) },
//            async { processAttachments(createdPost.id, input) }
        )

        deferred
            .awaitAll()

        return@coroutineScope PostResponse(post = updatedPost, success = true, message = "Пост успешно обновлен")

    }

//    private suspend fun processAttachments(postId: Long, input: CreatePostInput) {
//        input.attachments
//            ?.map { attachment ->
//                val path = filePersister.save(attachment.content(), attachment.filename(), postId)
//                postAttachmentRepository.savePostAttachment(postId, path)
//            }
//    }

    private suspend fun updateInterests(currentPost: Post, input: UpdatePostInput) = coroutineScope {
        val currentInterests = interestRepository.findByPostId(currentPost.id).map { it.id }.toHashSet()
        val deleted = currentInterests.filter { !input.interestIds.contains(it) }
        val newInterests = input.interestIds.filter { currentInterests.contains(it) }

        interestRepository.deleteInterestsFromPost(currentPost.id, deleted)
        newInterests.takeIf { it.isNotEmpty() }
            ?.let {
                interestRepository.addInterestsToPost(currentPost.id, it)
            }

    }

    private suspend fun updateTags(currentPost: Post, input: UpdatePostInput) {
        val currentTags: List<Tag> = tagsRepository.getTagsByPostId(currentPost.id)

        val currentTagNames = currentTags.map { it.name }.toHashSet()

        val deleted = currentTags.filter { !input.tags.contains(it.name) }
        val newTags = input.tags.filter { !currentTagNames.contains(it) }

        tagsRepository.deleteTagsByPostId(currentPost.id, deleted.map { it.id })

        newTags.takeIf { it.isNotEmpty() }
            ?.let {
                val tagIds = tagsRepository.createOrUpdate(newTags)
                tagsRepository.addTagsToPost(currentPost.id, tagIds)
            }
    }

    private suspend fun updateParsedLinks(currentPost: Post, input: UpdatePostInput) {
        val currentParsedLinks = parsedLinkRepository.findByPostId(currentPost.id)
        val currentLinksSet = currentParsedLinks.map { it.link }.toHashSet()

        val deleted = currentParsedLinks.filter { !input.parsedLinks.map { it.link }.contains(it.link) }

        parsedLinkRepository.deleteById(deleted.map { it.id })
        val newLinks = input.parsedLinks.filter { !currentLinksSet.contains(it.link) }

        newLinks.map { parsedLink ->
            val rq = CreateParsedLinkQueryData(
                postId = currentPost.id,
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

    private suspend fun processCollections(currentPost: Post, input: UpdatePostInput) {
        val currentPostCollections =
            collectionRepository.findAttachedCollectionsByPostId(currentPost.id).map { it.id }.toHashSet()
        val deleted = currentPostCollections.filter { !input.collectionIds.contains(it) }
        collectionRepository.deleteFromPost(currentPost.id, deleted)
        val new = input.collectionIds.filter { !currentPostCollections.contains(it) }
        new.takeIf { it.isNotEmpty() }
            ?.let {
                collectionRepository.addPostToCollections(currentPost.id, new)
            }
    }

}
