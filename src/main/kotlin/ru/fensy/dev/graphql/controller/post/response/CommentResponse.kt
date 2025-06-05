package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Comment

class CommentResponse(
    val comment: Comment, message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)