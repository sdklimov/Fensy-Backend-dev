package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Post

class PostResponse(
    val post: Post? = null, message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)