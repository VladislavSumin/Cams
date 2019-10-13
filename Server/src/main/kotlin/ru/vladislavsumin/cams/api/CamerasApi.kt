package ru.vladislavsumin.cams.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.vladislavsumin.cams.domain.CameraManager
import ru.vladislavsumin.cams.dto.CameraDto
import ru.vladislavsumin.cams.entity.toDto
import ru.vladislavsumin.cams.entity.toEntity

@RestController
@RequestMapping("/api/v1/cams")
class CamerasApi {
    @Autowired
    lateinit var cameraManager: CameraManager

    @GetMapping
    fun get(@RequestParam(required = false) includeDeleted: Boolean = false): List<CameraDto> {
        return cameraManager.getAll(includeDeleted).toDto()
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun add(@RequestBody camera: CameraDto): CameraDto {
        return cameraManager.save(camera.toEntity()).toDto()
    }

    @DeleteMapping
    fun delete(@RequestParam id: Long) {
        cameraManager.delete(id)
    }
}