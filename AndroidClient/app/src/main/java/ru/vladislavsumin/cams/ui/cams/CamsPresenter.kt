package ru.vladislavsumin.cams.ui.cams

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.vladislavsumin.cams.app.Injector
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

        updateCamsList()
        mCamsManager.fullUpdateDatabase()
                .subscribeOnIo()
                .subscribe({}, {})//Ignore error //TODO fix
                .autoDispose()//TODO
    }

    fun onClickRetry() {
        viewState.showProgressBar()
        updateCamsList()//TODO
    }

    private fun updateCamsList() {
        mCamsManager.observeAll()
                .map { list -> list.filter { !it.deleted } }//TODO add show delited
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.showList(it)
                }, {
                    Log.d(CamsActivity.TAG, "Failed load data: ${it.message}")
                    viewState.showError(it.message)//TODO
                })
                .autoDispose()
    }
}