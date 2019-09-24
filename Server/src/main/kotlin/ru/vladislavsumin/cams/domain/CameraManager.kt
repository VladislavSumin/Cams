package ru.vladislavsumin.cams.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.vladislavsumin.cams.entity.Camera
import ru.vladislavsumin.cams.repository.CameraRepository

@Service
class CameraManager {
    @Autowired
    lateinit var cameraRepository: CameraRepository

    fun getAll(includeDeleted: Boolean = false): Iterable<Camera> {
        return if (includeDeleted) cameraRepository.findAll()
        else cameraRepository.findAllByDeleted(false)
    }

    fun getEnabled() = cameraRepository.findAllByDeletedFalseAndEnabledTrue()


    fun save(camera: Camera) {
        cameraRepository.save(camera)
    }

    fun delete(id: Long): Boolean {
        val searchResult = cameraRepository.findById(id)

        if (!searchResult.isPresent) return false

        val camera = searchResult.get()
        cameraRepository.save(camera.copy(deleted = true))
        return true
    }
}