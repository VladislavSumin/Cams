package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.entity.Camera

interface CameraRepository : CrudRepository<Camera, Long> {
    fun findAllByDeleted(deleted: Boolean): Iterable<Camera>

    fun findAllByDeletedFalseAndEnabledTrue(): Iterable<Camera>
}