package ru.vladislavsumin.cams.database.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.vladislavsumin.cams.database.entity.CameraEntity

@Dao
interface CameraDao : BaseDao<CameraEntity> {

    @Query("SELECT * FROM cams")
    fun getAll(): Single<List<CameraEntity>>

    @Query("SELECT * FROM cams")
    fun observeAll(): Flowable<List<CameraEntity>>

    @Query("SELECT * FROM cams WHERE id = :id")
    fun getById(id: Long): Single<CameraEntity>

    @Query("DELETE FROM cams")
    fun deleteAll(): Completable
}