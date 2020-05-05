package ru.vladislavsumin.cams.domain

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.nio.file.Path

/**
 * Сервис кодирования видео из сырого h264 в mp4
 *
 * @author Sumin Vladislav
 * @version 1.0
 */
@Service
class VideoEncoderService @Autowired constructor(@Value("\${pFfmpegPath}") path: String) {
    private val executor: FFmpegExecutor
    private val ffprobe: FFprobe

    init {
        val ffmpeg = FFmpeg(path + "ffmpeg")
        ffprobe = FFprobe(path + "ffprobe")
        executor = FFmpegExecutor(ffmpeg, ffprobe)
    }

    /**
     * Перекодирует видео, ждет завершения операции прежде чем вернуть управление.
     *
     * @param input  - путь до исходного файла
     * @param output - путь до перекодированного файла (если файл существует, он будет перезаписан)
     */
    fun encode(input: Path, output: Path) {
        val builder = FFmpegBuilder()
                .setVerbosity(FFmpegBuilder.Verbosity.QUIET)
                .setInput(input.toAbsolutePath().toString())
                .addOutput(output.toAbsolutePath().toString())
                .setVideoFrameRate(24, 1)
                .addExtraArgs("-c", "copy")
                .done()

        executor.createJob(builder).run()
    }

    fun getDuration(file: Path): Double {
        return ffprobe.probe(file.toAbsolutePath().toString())
                .getFormat().duration
    }
}
