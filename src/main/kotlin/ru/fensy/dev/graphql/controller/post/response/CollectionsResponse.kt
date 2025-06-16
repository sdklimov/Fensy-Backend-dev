package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Collection

class CollectionsResponse(
    val collections: List<Collection>,
    message: String, success: Boolean = true,
): BaseResponse(message = message, success = success)