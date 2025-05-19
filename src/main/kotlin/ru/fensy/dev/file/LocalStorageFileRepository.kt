package ru.fensy.dev.file

import java.nio.file.Paths
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.time.OffsetDateTime
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class LocalStorageFileRepository : FilePersister {

    override suspend fun save(file: Flux<DataBuffer>, fileName: String, postId: Long): String {
        val path = Paths.get("${postId}_${OffsetDateTime.now()}_$fileName")
        DataBufferUtils.write(file, path, CREATE, WRITE, TRUNCATE_EXISTING)
            .then()
            .awaitFirstOrNull()
        return path.toString()
    }

}
