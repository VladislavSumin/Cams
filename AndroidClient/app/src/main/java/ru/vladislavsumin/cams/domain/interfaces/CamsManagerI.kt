package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.impl.CamsManager

interface CamsManagerI {
    fun observeAll(): Flowable<List<CameraEntity>>
    fun fullUpdateDatabase(): Completable
    fun observeDatabaseState(): Observable<CamsManager.DatabaseUpdateState>
}