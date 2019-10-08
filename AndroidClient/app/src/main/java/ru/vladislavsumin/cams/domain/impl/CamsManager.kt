package ru.vladislavsumin.cams.domain.impl

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.toEntity
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.cams.utils.SortedListDiff
import ru.vladislavsumin.core.utils.observeOnIo
import ru.vladislavsumin.core.utils.subscribeOnIo
import java.util.concurrent.TimeUnit

class CamsManager(
        private val repository: CameraDao,
        private val api: CamsApi
) : CamsManagerI {

    private val apiGetAllCompletable = api.getAll(true)
            .map { it.toEntity() }
            .toObservable()
            .share()

    private val apiGetAllFlowable =
            apiGetAllCompletable
                    .repeatWhen { it.delay(60, TimeUnit.SECONDS) }
                    .replay(1)
                    .refCount()
                    .toFlowable(BackpressureStrategy.LATEST)

    override fun observeAll(): Flowable<List<CameraEntity>> {
        return apiGetAllFlowable
    }


    //    private val allFlowable = api.getAll(true)
//            .map {
//                Thread.sleep(4000)
//                it.toEntity()
//            }
//            .repeatWhen { it.delay(20, TimeUnit.SECONDS) }
//            .doOnNext { Log.d("TEST", "data updated") }
//            .replay(1)
//            .refCount()


    private fun fullUpdateDatabase(): Completable {
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
                .observeOnIo()
                .map { dispatchChanges(it) }
                .ignoreElements()

    }

    private fun dispatchChanges(diff: SortedListDiff.Difference<CameraEntity>) {
        diff.deleted.forEach(repository::delete)
        diff.added.forEach(repository::insert)
        diff.modified.forEach(repository::update)
    }
}