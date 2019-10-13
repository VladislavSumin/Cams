package ru.vladislavsumin.cams.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import io.reactivex.Completable

interface BaseDao<Entity> {
    @Insert
    fun insert(entity: Entity)

    @Insert
    fun insert(entity: List<Entity>)

    @Update
    fun update(entity: Entity)

    @Update
    fun update(entity: List<Entity>)

    @Delete
    fun delete(entity: Entity)

    @Delete
    fun delete(entity: List<Entity>)


    @Insert
    fun observeInsert(entity: Entity): Completable

    @Insert
    fun observeInsert(entity: List<Entity>): Completable

    @Update
    fun observeUpdate(entity: Entity): Completable

    @Update
    fun observeUpdate(entity: List<Entity>): Completable

    @Delete
    fun observeDelete(entity: Entity): Completable

    @Delete
    fun observeDelete(entity: List<Entity>): Completable
}