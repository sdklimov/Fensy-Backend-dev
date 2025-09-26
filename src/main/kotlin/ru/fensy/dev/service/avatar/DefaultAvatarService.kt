package ru.fensy.dev.service.avatar

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

/**
 * Сервис стандартных аватаров
 */
@Service
class DefaultAvatarService(
    @Value("\${application.avatar.default-avatar-media-asset-ids}")
    private val defaultAvatarsMediaAssetsIds: Set<UUID>,
) {
    /**
     * Получить случайную аватарку из дефолтных
     */
    suspend fun getRandomAvatar(): UUID {
        if (defaultAvatarsMediaAssetsIds.isEmpty()) {
            throw IllegalStateException("Нет доступных дефолтных аватарок")
        }

        return defaultAvatarsMediaAssetsIds.random()
    }
}
