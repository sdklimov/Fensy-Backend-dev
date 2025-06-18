package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Post

class SearchPostsResponse(
    val posts: List<Post>? = emptyList(),
    val total: Long,
    val limit: Int,
    val offset: Int,
    message: String,
    success: Boolean = true,
) : BaseResponse(message = message, success = success)
