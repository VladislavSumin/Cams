package ru.vladislavsumin.cams.ui.cams

import com.arellomobile.mvp.MvpView
import ru.vladislavsumin.cams.entity.CameraDAO

interface CamsView : MvpView {
    fun showError(errorMsg: String? = null)
    fun showProgressBar()
    fun showList(cams: List<CameraDAO>)
}