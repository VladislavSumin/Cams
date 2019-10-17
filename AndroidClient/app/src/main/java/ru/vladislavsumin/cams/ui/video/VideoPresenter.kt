package ru.vladislavsumin.cams.ui.video

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.database.DatabaseUpdateState
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.domain.interfaces.RecordManagerI
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.*
import javax.inject.Inject

@InjectViewState
class VideoPresenter : BasePresenter<VideoView>() {
    companion object {
        private val TAG = tag<VideoPresenter>()
    }

    @Inject
    lateinit var mRecordManager: RecordManagerI

    @Inject
    lateinit var mCamsManager: CamsManagerI

    private var saveDisposable: Disposable? = null

    private val mShowOnlySaved = BehaviorSubject.createDefault(false)
    private val mDateFilter = BehaviorSubject.createDefault(0L)
    private val mCamsFilter = BehaviorSubject.createDefault(emptyList<CameraEntity>())

    var showOnlySaved: Boolean by mShowOnlySaved.delegate()
    var dateFilter: Long by mDateFilter.delegate()
    var camsFilter: List<CameraEntity> by mCamsFilter.delegate()


    init {
        Injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        mRecordManager.fullUpdateDatabaseAsync()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.dispose()
    }

    //***********************************************************************//
    //                             View actions                              //
    //***********************************************************************//

    fun onSelectRecord(record: RecordWithCamera) {
        viewState.playVideo(mRecordManager.getRecordUri(record.record))
    }

    fun observeRecords(): Flowable<List<RecordWithCamera>> {
        return Flowables.combineLatest(
                mRecordManager.observeAllWithCamera(),
                mShowOnlySaved.toFlowable(BackpressureStrategy.LATEST),
                mDateFilter.toFlowable(BackpressureStrategy.LATEST),
                mCamsFilter.toFlowable(BackpressureStrategy.LATEST),
                this::processRecord
        ).switchMap { it.subscribeOnComputation() }
    }

    fun onCancelSaveDialog() {
        saveDisposable?.dispose()
        saveDisposable = null
    }

    fun observeDatabaseStatus(): Observable<DatabaseUpdateState> {
        return mRecordManager.observeDatabaseState()
    }

    fun updateDatabase() {
        mRecordManager.fullUpdateDatabaseAsync()
    }

    fun onSaveRecord(id: Long, name: String?) {
        saveDisposable?.dispose()
        saveDisposable = mRecordManager.save(id, name)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.dismissSaveVideoDialog()
                }, {
                    //TODO add simple error logging class
                    Log.d(TAG, "error on save record", it)
                    viewState.stopSaveVideoDialogAnimation()
                    viewState.showToast("error")
                })
    }

    fun onDeleteRecord(id: Long) {
        saveDisposable?.dispose()
        saveDisposable = mRecordManager.delete(id)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.dismissSaveVideoDialog()
                }, {
                    Log.d(TAG, "error on delete record", it)
                    viewState.stopSaveVideoDialogAnimation()
                    viewState.showToast("error")
                })
    }

    fun observeCams(): Flowable<List<CameraEntity>> {
        return mCamsManager.observeAll()
                .subscribeOnIo()
                .observeOnComputation()
                .map { list ->
                    list
                            .filter { !it.deleted }
                            .sortedBy { it.name }
                }
    }

    //***********************************************************************//
    //                          Support functions                            //
    //***********************************************************************//

    private fun processRecord(records: List<RecordWithCamera>,
                              showOnlySaved: Boolean,
                              dateFilter: Long,
                              camsFilter: List<CameraEntity>): Flowable<List<RecordWithCamera>> {
        return Flowable.create({ emitter ->
            emitter.onNext(
                    records.asSequence()
                            .filter { !showOnlySaved || it.record.keepForever }
                            .filter {
                                if (dateFilter == 0L) true
                                else it.record.timestamp in
                                        dateFilter..(dateFilter + 24 * 60 * 60 * 1000)
                            }
                            .filter {
                                if (camsFilter.isEmpty()) true
                                else camsFilter.contains(it.camera)
                            }
                            .sortedByDescending { it.record.timestamp }
                            .toList()
            )
            emitter.onComplete()
        }, BackpressureStrategy.LATEST)
    }
}