package ru.fensy.dev.graphql.controller.post

import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import ru.fensy.dev.graphql.controller.post.response.PostResponse
import ru.fensy.dev.usecase.post.GetPostByIdUseCase

@Controller
class GetPostGraphQLController(
    private val getPostByIdUseCase: GetPostByIdUseCase,
) {

    @QueryMapping("getPost")
    suspend fun getPost(env: DataFetchingEnvironment): PostResponse {

        val postId = (((env.arguments as Map<String, Any>).entries.first().value) as String).toLong()
        val pageNumber = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageNumber")?.getValue<Int>() ?: 1
        val pageSize = env.queryDirectives.getImmediateAppliedDirective("page")
            .firstOrNull()?.getArgument("pageSize")?.getValue<Int>() ?: 25

        return getPostByIdUseCase.execute(postId)
    }

}
