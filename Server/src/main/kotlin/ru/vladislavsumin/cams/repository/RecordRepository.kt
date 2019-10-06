package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.entity.Record

interface RecordRepository : CrudRepository<Record, Long> {
}