package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.entity.CameraEntity

interface CameraRepository : CrudRepository<CameraEntity, Long> {
    fun findAllByDeleted(deleted: Boolean): Iterable<CameraEntity>

    fun findAllByDeletedFalseAndEnabledTrue(): Iterable<CameraEntity>
}