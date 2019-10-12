package ru.vladislavsumin.cams.domain.impl

import android.util.Log
import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.toEntity
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.cams.utils.SortedListDiff
import ru.vladislavsumin.core.utils.observeOnComputation
import ru.vladislavsumin.core.utils.observeOnIo
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag

class CamsManager(
        private val mRepository: CameraDao,
        private val mApi: CamsApi
) : CamsManagerI {
    companion object {
        private val TAG = tag<CamsManager>()
    }

    //***********************************************************************//
    //                                rx                                     //
    //***********************************************************************//

    // request full cams list from server
    private val mApiGetAllCompletable: Single<List<CameraEntity>> = mApi.getAll(true)
            .map { it.toEntity() }
            .toObservable()
            .doOnSubscribe { Log.d(TAG, "request cams list") }
            .doOnNext { Log.d(TAG, "received cams list: $it") }
            .doOnError { Log.d(TAG, "error on request cams list: $it") }
            .share()
            .firstOrError()

    private val mFullUpdateDatabaseCompletable: Completable = Singles.zip(
            //This tasks run one by one in same thread it is good solution
            mApiGetAllCompletable,
            mRepository.getAll()
    )
            .subscribeOnIo()
            .observeOnComputation()
            .map { SortedListDiff.calculateDif(it.second, it.first, CameraEntity.CREATOR) }
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
    //                             CamsManagerI                              //
    //***********************************************************************//

    override fun observeAll(): Flowable<List<CameraEntity>> = mRepository.observeAll()
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
    private fun dispatchChanges(diff: SortedListDiff.Difference<CameraEntity>) {
        diff.deleted.forEach(mRepository::delete)
        diff.added.forEach(mRepository::insert)
        diff.modified.forEach(mRepository::update)
    }

    //TODO move to other location
    enum class DatabaseUpdateState {
        UPDATED,
        UPDATING,
        NOT_UPDATED,
        ERROR
    }
}