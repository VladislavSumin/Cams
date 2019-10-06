package ru.vladislavsumin.cams.ui.cams

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.entity.toDAO
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.subscribeOnIo
import javax.inject.Inject


@InjectViewState
class CamsPresenter : BasePresenter<CamsView>() {
    @Inject
    lateinit var mCamsApi: CamsApi


    init {
        Injector.inject(this)

        updateCamsList()
    }

    fun onClickRetry() {
        viewState.showProgressBar()
        updateCamsList()
    }

    private fun updateCamsList() {
        mCamsApi.getAll()
                .map { it.toDAO() }
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.showList(it)
                }, {
                    Log.d(CamsActivity.TAG, "Failed load data: ${it.message}")
                    viewState.showError(it.message)
                })
                .autoDispose()
    }
}