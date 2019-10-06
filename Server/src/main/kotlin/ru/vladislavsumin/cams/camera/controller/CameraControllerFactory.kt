package ru.vladislavsumin.cams.camera.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.vladislavsumin.cams.dao.CameraDAO

@Component
class CameraControllerFactory @Autowired constructor(private val cameraRecorderFactory: CameraRecorderFactory) {
    fun createInstance(camera: CameraDAO): CameraController {
        return CameraController(camera, cameraRecorderFactory)
    }
}