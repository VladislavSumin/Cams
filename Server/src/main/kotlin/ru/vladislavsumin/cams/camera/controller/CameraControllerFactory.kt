package ru.vladislavsumin.cams.camera.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.vladislavsumin.cams.entity.CameraEntity

@Component
class CameraControllerFactory @Autowired constructor(private val cameraRecorderFactory: CameraRecorderFactory) {
    fun createInstance(camera: CameraEntity): CameraController {
        return CameraController(camera, cameraRecorderFactory)
    }
}