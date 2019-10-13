package ru.vladislavsumin.cams.repository

import org.springframework.data.repository.CrudRepository
import ru.vladislavsumin.cams.entity.RecordEntity

interface RecordRepository : CrudRepository<RecordEntity, Long> {
}