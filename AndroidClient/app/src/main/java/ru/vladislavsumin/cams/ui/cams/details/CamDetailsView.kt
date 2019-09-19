package ru.vladislavsumin.cams.ui.cams.details

import ru.vladislavsumin.cams.entity.Camera
import ru.vladislavsumin.core.mvp.BaseView

interface CamDetailsView : BaseView {
    fun setButtonsEnabled(enabled: Boolean)

    fun finishWithSaveResult(camera: Camera)
    fun finishWithDeleteResult(camera: Camera)
}