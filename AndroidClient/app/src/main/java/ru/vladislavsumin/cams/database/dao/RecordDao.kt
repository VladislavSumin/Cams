package ru.vladislavsumin.cams.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.RecordEntity

@Dao
interface RecordDao : BaseDao<RecordEntity> {
    @Query("SELECT * FROM records")
    fun getAll(): Single<List<RecordEntity>>

    @Query("SELECT * FROM records")
    fun observeAll(): Flowable<List<RecordEntity>>

    @Query("SELECT * FROM records")
    fun observeAllWithCamera(): Flowable<List<RecordWithCamera>>
}