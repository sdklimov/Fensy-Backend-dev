package ru.fensy.dev.graphql.resolver

import org.springframework.graphql.data.method.annotation.SchemaMapping
import ru.fensy.dev.annotation.FieldResolver
import ru.fensy.dev.domain.Country
import ru.fensy.dev.domain.Language
import ru.fensy.dev.domain.Post
import ru.fensy.dev.domain.User
import ru.fensy.dev.domain.UserSettings
import ru.fensy.dev.repository.*

@FieldResolver
class UserFieldResolver(
    private val postRepository: PostRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val countryRepository: CountriesRepository,
    private val languagesRepository: LanguagesRepository,
    private val fileRepository: FileRepository
) {

    @SchemaMapping(typeName = "User", field = "posts")
    suspend fun posts(user: User): List<Post> {
        return postRepository.findByAuthorId(user.id!!)
    }

    @SchemaMapping(typeName = "User", field = "avatar")
    suspend fun avatar(user: User): String {
        return "http://localhost:8080/api/v1/files/${fileRepository.findById(user.avatar!!)?.id}"
    }

    @SchemaMapping(typeName = "User", field = "settings")
    suspend fun settings(user: User): UserSettings {
        return userSettingsRepository.getByUserId(user.id!!)
    }

    @SchemaMapping(typeName = "User", field = "country")
    suspend fun country(user: User): Country {
        return countryRepository.getById(user.countryId)
    }

    @SchemaMapping(typeName = "User", field = "language")
    suspend fun language(user: User): Language {
        return languagesRepository.getById(user.languageId)
    }

}
