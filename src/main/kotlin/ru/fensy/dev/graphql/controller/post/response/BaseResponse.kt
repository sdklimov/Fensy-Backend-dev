package ru.fensy.dev.graphql.controller.post.response

open class BaseResponse(
    val success: Boolean = true,
    val message: String? = null,
)
