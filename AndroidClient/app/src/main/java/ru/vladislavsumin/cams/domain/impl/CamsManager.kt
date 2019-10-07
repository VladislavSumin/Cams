package ru.vladislavsumin.cams.domain.impl

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.vladislavsumin.cams.database.dao.CameraDao
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.toEntity
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.network.api.CamsApi
import java.util.concurrent.TimeUnit

class CamsManager(
        private val repository: CameraDao,
        private val api: CamsApi
) : CamsManagerI {

//    private val allFlowable = api.getAll(true)
//            .map {
//                Thread.sleep(4000)
//                it.toEntity()
//            }
//            .repeatWhen { it.delay(20, TimeUnit.SECONDS) }
//            .doOnNext { Log.d("TEST", "data updated") }
//            .replay(1)
//            .refCount()


    override fun observeAll(): Flowable<List<CameraEntity>> {
//        return allFlowable
//        return api.getAll(true)
//                .map { it.toEntity() }
//                .repeatWhen { it.delay(10, TimeUnit.SECONDS) }

    return TODO()
    }

}