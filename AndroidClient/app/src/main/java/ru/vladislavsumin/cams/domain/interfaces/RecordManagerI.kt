package ru.vladislavsumin.cams.domain.interfaces

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.vladislavsumin.cams.database.DatabaseUpdateState
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.entity.RecordEntity

interface RecordManagerI {
    fun observeAll(): Flowable<List<RecordEntity>>
    fun observeAllWithCamera(): Flowable<List<RecordWithCamera>>
    fun observeFullUpdateDatabase(): Completable
    fun fullUpdateDatabaseAsync()
    fun observeDatabaseState(): Observable<DatabaseUpdateState>

    fun save(id: Long, name: String?): Single<RecordEntity>
    fun delete(id: Long): Single<RecordEntity>

    fun getRecordUri(record: RecordEntity): Uri
    fun getRecordUri(id: Long): Uri
}