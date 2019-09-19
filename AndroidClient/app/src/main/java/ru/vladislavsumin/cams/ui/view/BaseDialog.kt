package ru.vladislavsumin.cams.ui.view

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.UiThread


abstract class BaseDialog(protected val context: Context) {
    protected abstract val dialog: AlertDialog

    @UiThread
    @CallSuper
    open fun show() = dialog.show()

    @UiThread
    @CallSuper
    open fun dismiss() = dialog.dismiss()
}