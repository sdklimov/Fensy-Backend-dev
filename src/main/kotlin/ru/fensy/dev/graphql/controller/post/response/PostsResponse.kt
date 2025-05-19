package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Post

class PostsResponse(
    val posts: List<Post>? = emptyList(), message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)
