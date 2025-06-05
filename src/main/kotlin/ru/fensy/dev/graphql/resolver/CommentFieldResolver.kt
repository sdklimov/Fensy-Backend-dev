package ru.fensy.dev.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Comment
import ru.fensy.dev.domain.PageRequest
import ru.fensy.dev.repository.CommentsRepository

@FieldResolver
class CommentFieldResolver(
    private val commentsRepository: CommentsRepository,
) {

    @SchemaMapping(typeName = "Comment", field = "children")
    suspend fun children(env: DataFetchingEnvironment): List<Comment> {
        val pageNumber = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageNumber")?.getValue<Int>() ?: 1
        val pageSize = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageSize")?.getValue<Int>() ?: 10 //todo: Вынести в проперти

        val comment = env.getSource<Comment>()
        return commentsRepository.getChildren(
            commentId = comment!!.id,
            pageRequest = PageRequest(pageNumber = pageNumber, pageSize = pageSize)
        )
    }

}
