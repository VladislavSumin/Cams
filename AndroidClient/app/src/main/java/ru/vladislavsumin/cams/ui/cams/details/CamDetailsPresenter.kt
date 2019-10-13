package ru.vladislavsumin.cams.ui.cams.details

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.cams.database.entity.toDTO
import ru.vladislavsumin.cams.domain.interfaces.CamsManagerI
import ru.vladislavsumin.cams.network.api.CamsApi
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag
import javax.inject.Inject

@InjectViewState
class CamDetailsPresenter : BasePresenter<CamDetailsView>() {
    companion object {
        private val TAG = tag<CamDetailsPresenter>()
    }

    @Inject
    //TODO переделать на работу с бд
    lateinit var mCamsApi: CamsApi

    @Inject
    lateinit var mCamsManager: CamsManagerI

    init {
        Injector.inject(this)
    }

    fun onClickSave(camera: CameraEntity) {
        viewState.setButtonsEnabled(false)
        mCamsManager.addOrModify(camera)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.finish()
                }, {
                    Log.d(TAG, "Error on save camera", it)
                    viewState.setButtonsEnabled(true)
                    viewState.showToast("error")
                })
                .autoDispose()
    }

    fun onClickDelete(camera: CameraEntity) {
        viewState.setButtonsEnabled(false)
        mCamsManager.delete(camera)
                .subscribeOnIo()
                .observeOnMainThread()
                .subscribe({
                    viewState.finish()
                }, {
                    Log.d(TAG, "Error on delete camera", it)
                    viewState.setButtonsEnabled(true)
                    viewState.showToast("error")
                })
                .autoDispose()
    }
}