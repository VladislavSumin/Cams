package ru.vladislavsumin.cams.ui.login

import ru.vladislavsumin.cams.dto.ServerInfoDto
import ru.vladislavsumin.core.mvp.BaseView

interface LoginView : BaseView {
    fun showCheckConnectionDialog()
    fun dismissCheckConnectionDialog()

    fun setConnectionDialogStateToWait()
    fun setConnectionDialogStateToSuccess()
    fun setConnectionDialogStateToError()

    fun setServerList(list: List<ServerInfoDto>)
}