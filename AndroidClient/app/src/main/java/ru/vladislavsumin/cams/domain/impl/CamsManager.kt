package ru.vladislavsumin.cams.domain.impl

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Observables
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

    private val fullUpdateDatabaseCompletable = createFullUpdateDatabase()

    override fun observeAll(): Flowable<List<CameraEntity>> {
        return repository.observeAll()
    }

    override fun fullUpdateDatabase(): Completable {
        return fullUpdateDatabaseCompletable
    }

    private fun createFullUpdateDatabase(): Completable {//TODO
        val allFromServer = api.getAll(true)
                .map { it.toEntity() }
                .subscribeOnIo()
                .toObservable()

        val allFromDatabase = repository.getAll()
                .subscribeOnIo()
                .toObservable()

        return Observables
                .combineLatest(allFromDatabase, allFromServer) { old, new ->
                    Pair(old, new)
                }
                .observeOn(Schedulers.computation())
                .map {
                    SortedListDiff.calculateDif(it.first, it.second, CameraEntity.CREATOR)
                }
                .log(TAG, "databaseFullUpdate")
                .observeOnIo()
                .map { dispatchChanges(it) }
                .log(TAG, "databaseFullUpdateDispached")
                .share()
                .ignoreElements()
    }

    private fun dispatchChanges(diff: SortedListDiff.Difference<CameraEntity>) {
        diff.deleted.forEach(repository::delete)
        diff.added.forEach(repository::insert)
        diff.modified.forEach(repository::update)
    }
}