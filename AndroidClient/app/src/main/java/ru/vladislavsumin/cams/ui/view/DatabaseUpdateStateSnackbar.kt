package ru.vladislavsumin.cams.ui.view

import android.view.View
import com.google.android.material.snackbar.Snackbar
import ru.vladislavsumin.cams.database.DatabaseUpdateState

class DatabaseUpdateStateSnackbar(private var rootView: View) {
    private var mSnackbar: Snackbar? = null
    private var mCallback: (() -> Unit)? = null

    fun showState(state: DatabaseUpdateState) {
        when (state) {
            DatabaseUpdateState.UPDATED -> mSnackbar?.dismiss()
            else -> setState(state)
        }
    }

    fun setCallback(callback: () -> Unit) {
        mCallback = callback
    }

    private fun setState(state: DatabaseUpdateState) {
        //TODO make custom layout

        val text = when (state) {//TODO move to resources
            DatabaseUpdateState.UPDATED -> throw RuntimeException("Unsupported state")
            DatabaseUpdateState.UPDATING -> "Updating database"
            DatabaseUpdateState.NOT_UPDATED -> "Database not updated"
            DatabaseUpdateState.ERROR -> "Error on update database"
        }

        val snackbar = Snackbar.make(rootView, text, Snackbar.LENGTH_INDEFINITE)

        if (state == DatabaseUpdateState.NOT_UPDATED)
            snackbar.setAction("Update") { mCallback?.invoke() }
        else if (state == DatabaseUpdateState.ERROR)
            snackbar.setAction("Retry") { mCallback?.invoke() }

        snackbar.show()

        mSnackbar = snackbar
    }
}