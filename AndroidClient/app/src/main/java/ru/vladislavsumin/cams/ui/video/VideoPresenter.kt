package ru.vladislavsumin.cams.ui.video

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.domain.RecordManager
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.cams.network.api.RecordsApi
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.*
import javax.inject.Inject

@InjectViewState
class VideoPresenter : BasePresenter<VideoView>() {
    companion object {
        private val TAG = tag<VideoPresenter>()
    }

    @Inject
    lateinit var mRecordsApi: RecordsApi

    @Inject
    lateinit var mRecordManager: RecordManager

    private var saveDisposable: Disposable? = null

    private val mShowOnlySaved = BehaviorSubject.createDefault(false)
    private val mDateFilter = BehaviorSubject.createDefault(0L)

    var showOnlySaved: Boolean by mShowOnlySaved.delegate()
    var dateFilter: Long by mDateFilter.delegate()

//    private val filter: RxListFilter<Record>

    init {
        Injector.inject(this)

//        filter = RxListFilter(mRecordManager.observeRecords(),
//                mShowOnlySaved.map { { record: Record -> !it || record.keepForever } },
//                mDateFilter.map { { record: Record -> it == 0L || record.timestamp in it..(it + 24 * 60 * 60 * 1000) } }
//        )

        updateVideoList()
    }

    fun onSelectRecord(record: Record) {
        viewState.playVideo(mRecordManager.getRecordUri(record))
    }

    fun observeRecordsToShow(): Observable<List<Record>> {
//        return filter.observeFiltered()
        return Observable.combineLatest(
                mShowOnlySaved,
                mDateFilter,
                BiFunction<Boolean, Long, Observable<List<Record>>> { showOnlySaved, dateFilter ->
                    mRecordManager.observeRecords()
                            .map { list ->
                                list.filter {
                                    if (showOnlySaved && !it.keepForever) return@filter false
                                    if (dateFilter == 0L) return@filter true
                                    else return@filter it.timestamp in dateFilter..(dateFilter + 24 * 60 * 60 * 1000)
                                }
                            }
                }
        ).flatMap {
            it.subscribeOn(Schedulers.computation())
        }
    }

    private fun updateVideoList() {
        mRecordManager.updateRecords()
                .observeOnMainThread()
                .subscribe({
                    viewState.showToast("Updated")
                }, {
                    viewState.showToast("Error on update")
                    Log.d(TAG, "Error on data get", it)
                }).autoDispose()
    }

    fun onCancelSaveDialog() {
        saveDisposable?.dispose()
        saveDisposable = null
    }

    fun onSaveRecord(id: Long, name: String?) {
        saveDisposable?.dispose()
        saveDisposable = mRecordsApi.save(id, name)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.dismissSaveVideoDialog()
                    viewState.updateSavedRecordListElement(it)
                }, {
                    //TODO add simple error logging class
                    Log.d(TAG, "error on save record", it)
                    viewState.stopSaveVideoDialogAnimation()
                    viewState.showToast("error")
                })
    }

    fun onDeleteRecord(id: Long) {
        saveDisposable?.dispose()
        saveDisposable = mRecordsApi.delete(id)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.dismissSaveVideoDialog()
                    viewState.updateSavedRecordListElement(it)
                }, {
                    Log.d(TAG, "error on save record", it)
                    viewState.stopSaveVideoDialogAnimation()
                    viewState.showToast("error")
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDisposable?.dispose()
    }
}