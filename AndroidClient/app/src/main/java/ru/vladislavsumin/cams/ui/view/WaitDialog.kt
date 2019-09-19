package ru.vladislavsumin.cams.ui.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import ru.vladislavsumin.cams.R

class WaitDialog(context: Context) : BaseDialog(context) {
    companion object {
        private const val LAYOUT = R.layout.dialog_wait
    }


    override val dialog: AlertDialog
    private val progressBar: ProgressBar
    private val image: ImageView
    private val text: TextView

    init {
        val view = LayoutInflater.from(context).inflate(LAYOUT, null)

        progressBar = view.findViewById(R.id.progress_bar)
        image = view.findViewById(R.id.image)
        text = view.findViewById(R.id.text)

        dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    @UiThread
    fun setState(state: State, @StringRes stringResId: Int) {
        setState(state, context.getString(stringResId))
    }

    @UiThread
    fun setState(state: State, text: String) {
        this.text.text = text
        when (state) {
            State.Wait -> {
                progressBar.visibility = View.VISIBLE
                image.visibility = View.GONE
            }

            State.Success -> {
                progressBar.visibility = View.GONE
                image.visibility = View.VISIBLE
                val icon = context.getDrawable(R.drawable.ic_check)
                icon!!.setTint(context.getColor(R.color.colorPrimaryDark))
                image.setImageDrawable(icon)
            }

            State.Error -> {
                progressBar.visibility = View.GONE
                image.visibility = View.VISIBLE
                val icon = context.getDrawable(R.drawable.ic_close)
                icon!!.setTint(context.getColor(android.R.color.holo_red_dark))
                image.setImageDrawable(icon)
            }
        }
    }

    enum class State {
        Wait,
        Error,
        Success,
    }
}