package ru.vladislavsumin.cams.domain

import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.cams.network.api.RecordsApi
import ru.vladislavsumin.cams.storage.CredentialStorage
import ru.vladislavsumin.core.utils.subscribeOnIo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Deprecated("write new one")
class RecordManager @Inject constructor(
        private val mCredentialStorage: CredentialStorage,
        private val recordsApi: RecordsApi
) {
    private val mRecords: BehaviorSubject<List<Record>> = BehaviorSubject.createDefault(emptyList())

    private val mUpdateRecords: Completable = recordsApi.getAll()
            .subscribeOnIo()
            .toObservable()
            .share()
            .doOnNext {
                mRecords.onNext(it)
            }
            .firstOrError()
            .ignoreElement()

    /**
     * get update records completable,
     * task run on IO thread
     */
    fun updateRecords(): Completable {
        return mUpdateRecords
    }

    fun observeRecords(): Observable<List<Record>> {
        return mRecords
    }

    fun getRecordUri(record: Record): Uri = getRecordUri(record.id)

    fun getRecordUri(id: Long): Uri =
            Uri.parse("${mCredentialStorage.serverAddress}/api/v1/records/record/$id")
}