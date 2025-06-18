package ru.fensy.dev.graphql.controller.userprofile.input

data class UserProfileInput(
    val fullName: String,
    val username: String,
    val email: String,
    val bio: String,
    val location: String,
    val website: String,
    val countryId: Long,
    val languageId: Long,
)
