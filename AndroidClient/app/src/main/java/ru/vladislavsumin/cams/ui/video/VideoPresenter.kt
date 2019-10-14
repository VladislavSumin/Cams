package ru.vladislavsumin.cams.ui.video

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
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

    private var saveDisposable: Disposable? = null

    private val mShowOnlySaved = BehaviorSubject.createDefault(false)
    private val mDateFilter = BehaviorSubject.createDefault(0L)

    var showOnlySaved: Boolean by mShowOnlySaved.delegate()
    var dateFilter: Long by mDateFilter.delegate()


    init {
        Injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        mRecordManager.fullUpdateDatabaseAsync()
    }

    fun onSelectRecord(record: RecordWithCamera) {
        viewState.playVideo(mRecordManager.getRecordUri(record.record))
    }

    fun observeRecordsToShow(): Flowable<List<RecordWithCamera>> {
        return Flowables.combineLatest(
                mRecordManager.observeAllWithCamera(),
                mShowOnlySaved.toFlowable(BackpressureStrategy.LATEST),
                mDateFilter.toFlowable(BackpressureStrategy.LATEST),
                this::processRecord
        ).switchMap { it.subscribeOnComputation() }
    }

    private fun processRecord(records: List<RecordWithCamera>,
                              showOnlySaved: Boolean,
                              dateFilter: Long): Flowable<List<RecordWithCamera>> {
        return Flowable.create({
            it.onNext(records.filter {
                if (showOnlySaved && !it.record.keepForever) return@filter false
                if (dateFilter == 0L) return@filter true
                else return@filter it.record.timestamp in dateFilter..(dateFilter + 24 * 60 * 60 * 1000)
            }.sortedByDescending { it.record.timestamp })
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    fun onCancelSaveDialog() {
        saveDisposable?.dispose()
        saveDisposable = null
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

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.dispose()
    }
}