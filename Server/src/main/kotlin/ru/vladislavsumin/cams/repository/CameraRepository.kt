package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.dao.CameraDAO

interface CameraRepository : CrudRepository<CameraDAO, Long> {
    fun findAllByDeleted(deleted: Boolean): Iterable<CameraDAO>

    fun findAllByDeletedFalseAndEnabledTrue(): Iterable<CameraDAO>
}