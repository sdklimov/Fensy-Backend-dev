package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Collection

class CollectionResponse(
    val collection: Collection,
    message: String, success: Boolean = true,
): BaseResponse(message = message, success = success)
