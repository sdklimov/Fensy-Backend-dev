package ru.fensy.dev.service.avatar

import jakarta.annotation.PostConstruct
import java.util.zip.ZipInputStream
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

/**
 * Сервис стандартных аватаров
 */
@Service
class DefaultAvatarService {

    private val defaultAvatars: MutableSet<ByteArray> = mutableSetOf()

    /**
     * Вычитывает архив с аватарками и сохраняет его в памяти.
     */
    @PostConstruct
    fun init() {
        val resource = ClassPathResource(AVATAR_PATH)
        val zipInputStream = ZipInputStream(resource.inputStream)

        zipInputStream.use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val img = zip.readBytes()
                defaultAvatars.add(img)
                entry = zip.nextEntry
            }
        }
    }

    /**
     * Получить случайную аватарку из дефолтных
     */
    fun getRandomAvatar() = defaultAvatars.random()

    companion object {
        private const val AVATAR_PATH = "avatars/iloveimg-compressed.zip"
    }

}
