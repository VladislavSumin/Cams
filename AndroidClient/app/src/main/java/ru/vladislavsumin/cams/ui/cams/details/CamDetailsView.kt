package ru.vladislavsumin.cams.ui.cams.details

import ru.vladislavsumin.cams.entity.CameraDAO
import ru.vladislavsumin.core.mvp.BaseView

interface CamDetailsView : BaseView {
    fun setButtonsEnabled(enabled: Boolean)

    fun finishWithSaveResult(camera: CameraDAO)
    fun finishWithDeleteResult(camera: CameraDAO)
}