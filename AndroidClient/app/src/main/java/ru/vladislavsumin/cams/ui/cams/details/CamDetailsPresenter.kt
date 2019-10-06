package ru.vladislavsumin.cams.ui.cams.details

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.entity.CameraDAO
import ru.vladislavsumin.cams.entity.toDTO
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
    lateinit var mCamsApi: CamsApi

    init {
        Injector.inject(this)
    }

    fun onClickSave(camera: CameraDAO) {
        viewState.setButtonsEnabled(false)
        mCamsApi.add(camera.toDTO())
            .subscribeOnIo()
            .observeOnMainThread()
            .subscribe({
                viewState.finishWithSaveResult(camera)
            }, {
                Log.d(TAG, "Error on save camera", it)
                viewState.setButtonsEnabled(true)
                viewState.showToast("error")
            })
            .autoDispose()
    }

    fun onClickDelete(camera: CameraDAO) {
        viewState.setButtonsEnabled(false)
        mCamsApi.delete(camera.id)
            .subscribeOnIo()
            .observeOnMainThread()
            .subscribe({
                viewState.finishWithDeleteResult(camera)
            }, {
                Log.d(TAG, "Error on delete camera", it)
                viewState.setButtonsEnabled(true)
                viewState.showToast("error")
            })
            .autoDispose()
    }
}