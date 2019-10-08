package ru.vladislavsumin.cams.domain.impl

import android.util.Log
import androidx.annotation.WorkerThread
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.toEntity
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.cams.utils.SortedListDiff
import ru.vladislavsumin.core.utils.log
import ru.vladislavsumin.core.utils.observeOnIo
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag

class CamsManager(
        private val repository: CameraDao,
        private val api: CamsApi
) : CamsManagerI {
    companion object {
        private val TAG = tag<CamsManager>()
    }

    // request full cams list from server
    private val apiGetAllCompletable = api.getAll(true)
            .map { it.toEntity() }
            .toObservable()
            .doOnSubscribe { Log.d(TAG, "request cams list") }
            .doOnNext { Log.d(TAG, "received cams list: $it") }
            .doOnError { Log.d(TAG, "error on request cams list: $it") }
            .share()
            .firstOrError()

    private val fullUpdateDatabaseCompletable = createFullUpdateDatabase()

    override fun observeAll(): Flowable<List<CameraEntity>> {
        return repository.observeAll()
    }

    override fun fullUpdateDatabase(): Completable {
        return fullUpdateDatabaseCompletable
    }

    private fun createFullUpdateDatabase(): Completable {//TODO
        val allFromDatabase = repository.getAll()
                .subscribeOnIo()

        return Singles
                .zip(allFromDatabase, apiGetAllCompletable) { old, new ->
                    Pair(old, new)
                }
                .observeOn(Schedulers.computation())
                .map {
                    SortedListDiff.calculateDif(it.first, it.second, CameraEntity.CREATOR)
                }
//                .log(TAG, "databaseFullUpdate")
                .observeOnIo()
                .map { dispatchChanges(it) }
//                .log(TAG, "databaseFullUpdateDispached")
                .toObservable()
                .share()
                .ignoreElements()
    }

    /**
     * Dispatch changes from diff to database
     */
    @WorkerThread
    private fun dispatchChanges(diff: SortedListDiff.Difference<CameraEntity>) {
        diff.deleted.forEach(repository::delete)
        diff.added.forEach(repository::insert)
        diff.modified.forEach(repository::update)
    }

    enum class UpdateState {
        Updated,
        Updating,
        Error
    }
}