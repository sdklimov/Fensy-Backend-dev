package ru.fensy.dev.graphql.controller.user.response

import ru.fensy.dev.domain.User
import ru.fensy.dev.graphql.controller.post.response.BaseResponse

class UserResponse(
     val user: User? = null, message: String, success: Boolean = true
 ): BaseResponse(message = message, success = success)
