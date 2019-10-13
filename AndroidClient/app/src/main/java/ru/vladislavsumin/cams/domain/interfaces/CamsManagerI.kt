package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.impl.CamsManager

interface CamsManagerI {
    fun observeAll(): Flowable<List<CameraEntity>>
    fun observeFullUpdateDatabase(): Completable
    fun fullUpdateDatabaseAsync()
    fun observeDatabaseState(): Observable<CamsManager.DatabaseUpdateState>

    fun addOrModify(camera: CameraEntity): Single<CameraEntity>
    fun delete(camera: CameraEntity): Completable
}