package ru.vladislavsumin.cams.domain.impl

import android.util.Log
import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.database.DatabaseUpdateState
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.dao.RecordDao
import ru.vladislavsumin.cams.database.entity.RecordEntity
import ru.vladislavsumin.cams.database.entity.toEntity
import ru.vladislavsumin.cams.domain.interfaces.RecordManagerI
import ru.vladislavsumin.cams.network.api.RecordsApiV2
import ru.vladislavsumin.cams.utils.SortedListDiff
import ru.vladislavsumin.core.utils.observeOnComputation
import ru.vladislavsumin.core.utils.observeOnIo
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag

class RecordManager(
        private val mRepository: RecordDao,
        private val mApi: RecordsApiV2
) : RecordManagerI {
    companion object {
        private val TAG = tag<RecordManager>()
    }

    //***********************************************************************//
    //                                rx                                     //
    //***********************************************************************//

    // request full cams list from server
    private val mApiGetAllCompletable: Single<List<RecordEntity>> = mApi.getAll()
            .map { it.toEntity() }
            .toObservable()
            .doOnSubscribe { Log.d(TAG, "request records list") }
            .doOnNext { Log.d(TAG, "received records list: $it") }
            .doOnError { Log.d(TAG, "error on request records list: $it") }
            .share()
            .firstOrError()

    private val mFullUpdateDatabaseCompletable: Completable = Singles.zip(
            //This tasks run one by one in same thread it is good solution
            mApiGetAllCompletable,
            mRepository.getAll()
    )
            .subscribeOnIo()
            .observeOnComputation()
            .map { SortedListDiff.calculateDif(it.second, it.first, RecordEntity.Companion) }
            .observeOnIo()
            .map(this::dispatchChanges)
            .toObservable()
            .doOnSubscribe { mUpdateStateObservable.onNext(DatabaseUpdateState.UPDATING) }
            .doOnComplete { mUpdateStateObservable.onNext(DatabaseUpdateState.UPDATED) }
            .doOnError { mUpdateStateObservable.onNext(DatabaseUpdateState.ERROR) }
            .share()
            .ignoreElements()

    private val mUpdateStateObservable = BehaviorSubject.createDefault(DatabaseUpdateState.NOT_UPDATED)

    //***********************************************************************//
    //                            RecordManagerI                             //
    //***********************************************************************//

    override fun observeAll(): Flowable<List<RecordEntity>> = mRepository.observeAll()
            .distinctUntilChanged()

    override fun observeAllWithCamera(): Flowable<List<RecordWithCamera>> =
            mRepository.observeAllWithCamera().distinctUntilChanged()

    override fun observeFullUpdateDatabase(): Completable = mFullUpdateDatabaseCompletable

    override fun fullUpdateDatabaseAsync() {
        mFullUpdateDatabaseCompletable.onErrorComplete().subscribe()
    }

    override fun observeDatabaseState(): Observable<DatabaseUpdateState> = mUpdateStateObservable

    //***********************************************************************//
    //                          Support functions                            //
    //***********************************************************************//

    /**
     * Dispatch changes from diff to database
     */
    @WorkerThread
    private fun dispatchChanges(diff: SortedListDiff.Difference<RecordEntity>) {
        mRepository.delete(diff.deleted)
        mRepository.insert(diff.added)
        mRepository.update(diff.modified)
    }
}