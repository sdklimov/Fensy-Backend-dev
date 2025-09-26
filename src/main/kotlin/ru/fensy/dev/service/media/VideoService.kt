package ru.fensy.dev.service.media

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.PipeInput
import com.github.kokorin.jaffree.ffmpeg.PipeOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import ru.fensy.dev.constants.Constants.DEFAULT_VIDEO_FORMAT
import ru.fensy.dev.constants.Constants.DEFAULT_VIDEO_PREVIEW_FORMAT
import java.io.ByteArrayOutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.nio.file.Path
import java.nio.file.Paths

private const val BEST_QUALITY = 2
private const val WORST_QUALITY = 31

@Service
class VideoService {
    var ffmpegBin: Path = Paths.get("/usr/bin")

    suspend fun createThumbnail(inputStream: Flow<DataBuffer>, width: Int, quality: Float): ByteArray =
        withContext(Dispatchers.IO) {
            val ffmpegInputSink = PipedOutputStream()
            val ffmpegInputSource = PipedInputStream(ffmpegInputSink)

            val ffmpegOutputSink = PipedOutputStream()
            val ffmpegOutputSource = PipedInputStream(ffmpegOutputSink)

            val inputJob = launch(Dispatchers.IO) {
                ffmpegInputSink.use { DataBufferUtils.write(inputStream.asFlux(), it).blockLast() }
            }

            val ffmpegJob = launch(Dispatchers.IO) {
                ffmpegOutputSink.use { ffmpegOutputSink ->
                    FFmpeg.atPath(ffmpegBin)
                        .addInput(PipeInput.pumpFrom(ffmpegInputSource).setFormat(DEFAULT_VIDEO_FORMAT))
                        .addOutput(PipeOutput.pumpTo(ffmpegOutputSink).setFormat("image2"))
                        .addArguments("-vf", "thumbnail,scale=$width:-1")
                        .addArguments("-frames:v", "1")
                        .addArguments("-f", "image2")
                        .addArguments("-q:v", "${BEST_QUALITY + WORST_QUALITY - quality * WORST_QUALITY}")
                        .addArguments("-c:v", DEFAULT_VIDEO_PREVIEW_FORMAT)
                        .execute()
                }
            }

            val buffer = ByteArrayOutputStream()
            val buf = ByteArray(4096)
            var bytesRead: Int
            ffmpegOutputSource.use { input ->
                while (input.read(buf).also { bytesRead = it } != -1) {
                    buffer.write(buf, 0, bytesRead)
                }
            }

            inputJob.join()
            ffmpegJob.join()

            return@withContext buffer.toByteArray()
        }
}