package ru.vladislavsumin.cams.ui.cams.details

import ru.vladislavsumin.cams.database.entity.CameraEntity
import ru.vladislavsumin.core.mvp.BaseView

interface CamDetailsView : BaseView {
    fun setButtonsEnabled(enabled: Boolean)

    fun finishWithSaveResult(camera: CameraEntity)
    fun finishWithDeleteResult(camera: CameraEntity)
}