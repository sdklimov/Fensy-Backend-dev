package ru.fensy.dev.graphql.controller.auth.response

import ru.fensy.dev.graphql.controller.post.response.BaseResponse

class AuthResponse(
    created: Boolean,
    accessToken: String? = null,
    message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)
