package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.dao.Record

interface RecordRepository : CrudRepository<Record, Long> {
}