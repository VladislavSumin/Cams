package ru.vladislavsumin.cams.camera.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.vladislavsumin.cams.entity.Camera

@Component
class CameraControllerFactory @Autowired constructor(private val cameraRecorderFactory: CameraRecorderFactory) {
    fun createInstance(camera: Camera): CameraController {
        return CameraController(camera, cameraRecorderFactory)
    }
}