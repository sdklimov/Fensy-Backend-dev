package ru.fensy.dev.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "application.media.compression")
data class MediaCompressionProperties(
    var imagePostAttachment: ImageSizes = ImageSizes(),
    var videoPostAttachment: VideoPostAttachmentSizes = VideoPostAttachmentSizes(),
    var avatar: ImageSizes = ImageSizes()
) {
    data class VideoPostAttachmentSizes(
        var playback720p: VideoSizeConfig = VideoSizeConfig(),
        var loop: LoopSizeConfig = LoopSizeConfig(),
        var thumbnail: ImageSizeConfig = ImageSizeConfig()
    )

    data class ImageSizes(
        var large: ImageSizeConfig = ImageSizeConfig(),
        var medium: ImageSizeConfig = ImageSizeConfig(),
        var thumbnail: ImageSizeConfig = ImageSizeConfig()
    )

    data class ImageSizeConfig(
        var width: Int = 0,
        var quality: Float = 0.0f
    )

    data class VideoSizeConfig(
        var height: Int = 0,
        var crf: Int = 0
    )

    data class LoopSizeConfig(
        var width: Int = 0,
        var crf: Int = 0
    )
}
