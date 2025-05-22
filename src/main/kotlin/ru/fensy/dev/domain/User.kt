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
    val avatar: String? = null,
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
)
