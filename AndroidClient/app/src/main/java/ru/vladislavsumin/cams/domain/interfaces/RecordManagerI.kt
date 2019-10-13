package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.vladislavsumin.cams.database.DatabaseUpdateState
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.entity.RecordEntity

interface RecordManagerI {
    fun observeAll(): Flowable<List<RecordEntity>>
    fun observeAllWithCamera(): Flowable<List<RecordWithCamera>>
    fun observeFullUpdateDatabase(): Completable
    fun fullUpdateDatabaseAsync()
    fun observeDatabaseState(): Observable<DatabaseUpdateState>
}