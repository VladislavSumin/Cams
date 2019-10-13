package ru.vladislavsumin.cams.ui.cams.details

import ru.vladislavsumin.core.mvp.BaseView

interface CamDetailsView : BaseView {
    fun setButtonsEnabled(enabled: Boolean)
}