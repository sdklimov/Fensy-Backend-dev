package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Interest

class InterestsResponse(
    val interests: List<Interest>,
    message: String, success: Boolean = true,
): BaseResponse(message = message, success = success)