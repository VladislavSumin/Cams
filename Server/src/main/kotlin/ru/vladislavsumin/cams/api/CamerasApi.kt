package ru.vladislavsumin.cams.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.vladislavsumin.cams.domain.CameraManager
import ru.vladislavsumin.cams.entity.toDTO
import ru.vladislavsumin.cams.dto.CameraDTO
import ru.vladislavsumin.cams.entity.toEntity

@RestController
@RequestMapping("/api/v1/cams")
class CamerasApi {
    @Autowired
    lateinit var cameraManager: CameraManager

    @GetMapping
    fun get(@RequestParam(required = false) includeDeleted: Boolean = false): List<CameraDTO> {
        return cameraManager.getAll(includeDeleted).toDTO()
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun add(@RequestBody camera: CameraDTO): CameraDTO {
        return cameraManager.save(camera.toEntity()).toDTO()
    }

    @DeleteMapping
    fun delete(@RequestParam id: Long) {
        cameraManager.delete(id)
    }
}