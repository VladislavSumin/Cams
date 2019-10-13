package ru.vladislavsumin.cams.database.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.vladislavsumin.cams.database.entity.CameraEntity

@Dao
interface CameraDao {

    @Query("SELECT * FROM cams")
    fun getAll(): Single<List<CameraEntity>>

    @Query("SELECT * FROM cams")
    fun observeAll(): Flowable<List<CameraEntity>>

    @Query("SELECT * FROM cams WHERE id = :id")
    fun getById(id: Long): Single<CameraEntity>

    @Insert
    fun insert(camera: CameraEntity)

    @Update
    fun update(camera: CameraEntity)

    @Delete
    fun delete(camera: CameraEntity)

    @Insert
    fun observeInsert(camera: CameraEntity): Completable

    @Update
    fun observeUpdate(camera: CameraEntity): Completable

    @Delete
    fun observeDelete(camera: CameraEntity): Completable

    @Query("DELETE FROM cams")
    fun deleteAll(): Completable
}