package ru.fensy.dev.file

import org.springframework.core.io.buffer.DataBuffer
import reactor.core.publisher.Flux

interface FilePersister {

    suspend fun save(file: Flux<DataBuffer>, fileName: String, postId: Long): String

}
