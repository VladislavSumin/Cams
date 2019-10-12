package ru.vladislavsumin.cams.ui.cams

import com.arellomobile.mvp.InjectViewState
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.impl.CamsManager
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.subscribeOnIo
import javax.inject.Inject


@InjectViewState
class CamsPresenter : BasePresenter<CamsView>() {
    @Inject
    lateinit var mCamsManager: CamsManagerI

    init {
        Injector.inject(this)
        mCamsManager.fullUpdateDatabaseAsync()
    }

    //***********************************************************************//
    //                             View actions                              //
    //***********************************************************************//

    fun observeCamsList(): Flowable<List<CameraEntity>> {
        return mCamsManager.observeAll()
                .map(this::sortCams)
                .subscribeOnIo()
    }

    fun observeDatabaseStatus(): Observable<CamsManager.DatabaseUpdateState> =
            mCamsManager.observeDatabaseState()

    fun updateDatabase() {
        mCamsManager.fullUpdateDatabaseAsync()
    }

    //***********************************************************************//
    //                          Support functions                            //
    //***********************************************************************//

    private fun sortCams(cams: List<CameraEntity>): List<CameraEntity> {
        return cams.sortedWith(Comparator { o1, o2 ->
            val deleted = o1.deleted.compareTo(o2.deleted)
            if (deleted != 0) return@Comparator deleted

            val enabled = o1.enabled.compareTo(o2.enabled)
            if (enabled != 0) return@Comparator enabled

            o1.name.compareTo(o2.name)
        })
    }
}