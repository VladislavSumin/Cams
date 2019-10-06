package ru.vladislavsumin.cams.camera.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.vladislavsumin.cams.domain.RecordManager
import ru.vladislavsumin.cams.domain.VideoEncoderService
import ru.vladislavsumin.cams.dao.CameraDAO
import java.nio.file.Paths

@Component
class CameraRecorderFactory @Autowired constructor(
    @Value("\${pTmpRecordPath}") path: String,
    private val videoEncoderService: VideoEncoderService,
    private val recordManager: RecordManager) {

    private val path = Paths.get(path).apply { toFile().mkdirs() }

    fun createInstance(camera: CameraDAO): CameraRecorder {
        return CameraRecorder(camera, videoEncoderService, recordManager, path)
    }
}