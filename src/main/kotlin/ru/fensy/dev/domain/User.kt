package ru.fensy.dev.domain

import java.time.OffsetDateTime

/**
 * Пользователь
 */
data class User(
    val id: Long? = null,
    val isVerified: Boolean,
    val fullName: String? = null,
    val username: String,
    val email: String? = null,
    val avatar: ByteArray? = null,
    val bio: String? = null,
    val location: String? = null,
    val role: UserRole,
    val website: String? = null,
    val countryId: Long,
    val languageId: Long,
    val telegramId: String? = null,
    val tonWalletId: String? = null,
    val yandexId: String? = null,
    val vkId: String? = null,
    val isActive: Boolean,
    val lastLoginAt: OffsetDateTime,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (isVerified != other.isVerified) return false
        if (countryId != other.countryId) return false
        if (languageId != other.languageId) return false
        if (isActive != other.isActive) return false
        if (fullName != other.fullName) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (!avatar.contentEquals(other.avatar)) return false
        if (bio != other.bio) return false
        if (location != other.location) return false
        if (role != other.role) return false
        if (website != other.website) return false
        if (telegramId != other.telegramId) return false
        if (tonWalletId != other.tonWalletId) return false
        if (yandexId != other.yandexId) return false
        if (vkId != other.vkId) return false
        if (lastLoginAt != other.lastLoginAt) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + isVerified.hashCode()
        result = 31 * result + countryId.hashCode()
        result = 31 * result + languageId.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + (fullName?.hashCode() ?: 0)
        result = 31 * result + username.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        result = 31 * result + (bio?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + role.hashCode()
        result = 31 * result + (website?.hashCode() ?: 0)
        result = 31 * result + (telegramId?.hashCode() ?: 0)
        result = 31 * result + (tonWalletId?.hashCode() ?: 0)
        result = 31 * result + (yandexId?.hashCode() ?: 0)
        result = 31 * result + (vkId?.hashCode() ?: 0)
        result = 31 * result + lastLoginAt.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}