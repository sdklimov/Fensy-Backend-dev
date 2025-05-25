package ru.fensy.dev.graphql.controller.auth.response

import ru.fensy.dev.graphql.controller.post.response.BaseResponse

class RefreshResponse(
    val accessToken: String? = null,
    message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)
