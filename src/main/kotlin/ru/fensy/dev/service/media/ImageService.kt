package ru.fensy.dev.service.media

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import ru.fensy.dev.constants.Constants
import ru.fensy.dev.properties.MediaCompressionProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Результат сжатия изображения в виде потока
 */
data class CompressedImageStreamResult(
    val byteArray: ByteArray,
    val width: Int,
    val height: Int,
    val sizeBytes: Long
)

@Service
class ImageService {

    /**
     * Сжимает изображение из потока и возвращает сжатое изображение в виде массива байт
     */
    suspend fun compressImageStream(
        imageStream: Flow<DataBuffer>,
        imageSizeConfig: MediaCompressionProperties.ImageSizeConfig
    ): CompressedImageStreamResult? = withContext(Dispatchers.IO) {
            val imageBytes = flowToByteArray(imageStream)
            val imageInputStream = ByteArrayInputStream(imageBytes)

            val (originalWidth, originalHeight) = getImageDimensions(imageInputStream)

            if (originalWidth <= imageSizeConfig.width) {
                return@withContext null
            }

            val aspectRatio = originalHeight.toDouble() / originalWidth.toDouble()
            val newWidth = imageSizeConfig.width
            val newHeight = (newWidth * aspectRatio).toInt()

            val outputStream = ByteArrayOutputStream()

            Thumbnails.of(ByteArrayInputStream(imageBytes))
                .size(newWidth, newHeight)
                .outputFormat(Constants.DEFAULT_IMAGE_FORMAT)
                .outputQuality(imageSizeConfig.quality)
                .toOutputStream(outputStream)


            val compressedBytes = outputStream.toByteArray()

            CompressedImageStreamResult(
                byteArray = outputStream.toByteArray(),
                width = newWidth,
                height = newHeight,
                sizeBytes = compressedBytes.size.toLong()
            )
    }

    private fun getImageDimensions(inputStream: InputStream): Pair<Int, Int> {
        ImageIO.createImageInputStream(inputStream).use { iis ->
            val readers = ImageIO.getImageReaders(iis)
            if (readers.hasNext()) {
                val reader = readers.next()
                try {
                    reader.input = iis
                    return Pair(reader.getWidth(0), reader.getHeight(0))
                } finally {
                    reader.dispose()
                }
            }
        }
        throw IllegalArgumentException("Не удалось прочитать размеры изображения")
    }

    private suspend fun flowToByteArray(dataBufferFlow: Flow<DataBuffer>): ByteArray = withContext(Dispatchers.IO) {
        val buffers = dataBufferFlow.toList()
        val outputStream = ByteArrayOutputStream()
        for (buffer in buffers) {
            try {
                val byteBuffers = buffer.readableByteBuffers()
                for (byteBuffer in byteBuffers) {
                    val bytes = ByteArray(byteBuffer.remaining())
                    byteBuffer.get(bytes)
                    outputStream.write(bytes)
                }
            } finally {
                DataBufferUtils.release(buffer)
            }
        }
        outputStream.toByteArray()
    }
}
