package ru.vladislavsumin.cams.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.vladislavsumin.cams.camera.controller.CameraController
import ru.vladislavsumin.cams.camera.controller.CameraControllerFactory
import ru.vladislavsumin.cams.dao.CameraDAO
import ru.vladislavsumin.cams.utils.logger
import javax.annotation.PreDestroy

@Service
class CameraConnectionManager @Autowired constructor(
    private val cameraManager: CameraManager,
    private val cameraControllerFactory: CameraControllerFactory
) {
    private val cams: MutableSet<CameraController> = HashSet()


    init {
        loadControllers(cameraManager.getAll())
        startControllers()
    }

    @PreDestroy
    fun destroy() {
        cams.forEach { it.stop() }
    }

    private fun loadControllers(config: Iterable<CameraDAO>) {
        config.forEach { cams.add(cameraControllerFactory.createInstance(it)) }
    }

    private fun startControllers() {
        cams.forEach { it.start() }
    }

    companion object {
        private val log = logger<CameraConnectionManager>()
    }
}