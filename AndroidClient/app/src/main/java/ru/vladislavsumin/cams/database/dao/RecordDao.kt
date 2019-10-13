package ru.vladislavsumin.cams.database.dao

import androidx.room.Dao
import ru.vladislavsumin.cams.database.entity.RecordEntity

@Dao
interface RecordDao : BaseDao<RecordEntity> {
}