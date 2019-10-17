package ru.vladislavsumin.cams.domain.impl

import android.net.Uri
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
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.domain.interfaces.RecordManagerI
import ru.vladislavsumin.cams.network.api.RecordsApi
import ru.vladislavsumin.cams.storage.CredentialStorage
import ru.vladislavsumin.cams.utils.SortedListDiff
import ru.vladislavsumin.core.utils.observeOnComputation
import ru.vladislavsumin.core.utils.observeOnIo
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag

class RecordManager(
        private val mRepository: RecordDao,
        private val mApi: RecordsApi,
        private val mCredentialStorage: CredentialStorage,
        private val mCameraManager: CamsManagerI
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

    private val mFullUpdateDatabaseCompletable: Completable =
            mCameraManager.observeFullUpdateDatabase()
                    .andThen(
                            Singles.zip(
                                    //This tasks run one by one in same thread it is good solution
                                    mApiGetAllCompletable,
                                    mRepository.getAll()
                            )

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

    override fun save(id: Long, name: String?): Single<RecordEntity> {
        return mApi.save(id, name).flatMap {
            val entity = it.toEntity()
            mRepository.observeUpdate(entity)
                    .onErrorComplete()
                    .andThen(Single.just(entity))
        }
    }

    override fun delete(id: Long): Single<RecordEntity> {
        return mApi.delete(id).flatMap {
            val entity = it.toEntity()
            mRepository.observeUpdate(entity)
                    .onErrorComplete()
                    .andThen(Single.just(entity))
        }
    }

    override fun getRecordUri(record: RecordEntity): Uri = getRecordUri(record.id)

    override fun getRecordUri(id: Long): Uri =
            Uri.parse("${mCredentialStorage.serverAddress}/api/v1/records/record/$id")

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