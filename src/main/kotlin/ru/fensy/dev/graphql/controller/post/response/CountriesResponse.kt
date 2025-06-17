package ru.fensy.dev.graphql.controller.post.response

import ru.fensy.dev.domain.Country

class CountriesResponse(
    val countries: List<Country>,
    message: String, success: Boolean = true,
) : BaseResponse(message = message, success = success)
