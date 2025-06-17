package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Language

class LanguagesResponse(
    val languages: List<Language>,
    message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)
