package ru.vladislavsumin.cams.ui.video

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.domain.interfaces.RecordManagerI
import ru.vladislavsumin.cams.network.api.RecordsApiV1
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.*
import javax.inject.Inject

@InjectViewState
class VideoPresenter : BasePresenter<VideoView>() {
    companion object {
        private val TAG = tag<VideoPresenter>()
    }

    @Inject
    lateinit var mRecordsApi: RecordsApiV1

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

    fun observeRecordsToShow(): Observable<List<RecordWithCamera>> {
        return Observable.combineLatest(
                mShowOnlySaved,
                mDateFilter,
                BiFunction<Boolean, Long, Observable<List<RecordWithCamera>>> { showOnlySaved, dateFilter ->
                    mRecordManager.observeAllWithCamera().toObservable()
                            .map { list ->
                                list.filter {
                                    if (showOnlySaved && !it.record.keepForever) return@filter false
                                    if (dateFilter == 0L) return@filter true
                                    else return@filter it.record.timestamp in dateFilter..(dateFilter + 24 * 60 * 60 * 1000)
                                }
                            }
                    //TODO fix rx chain!
                }
        ).flatMap {
            it.subscribeOn(Schedulers.computation())
        }
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
//                    viewState.updateSavedRecordListElement(it)//TODO !!!!!!!!!!!!!!!
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
//                    viewState.updateSavedRecordListElement(it) //TODO !!!!!!!!!!!!!!!!!!
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