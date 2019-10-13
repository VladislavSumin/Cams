package ru.vladislavsumin.cams.ui.video

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.UiThread
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputLayout
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.database.combined.RecordWithCamera
import ru.vladislavsumin.cams.database.entity.RecordEntity
import ru.vladislavsumin.cams.entity.Record
import ru.vladislavsumin.cams.ui.view.BaseDialog

class SaveVideoDialog(context: Context, record: RecordEntity) : BaseDialog(context) {
    companion object {
        private const val LAYOUT = R.layout.dialog_save
    }

    override val dialog: AlertDialog

    private val btnSave: CircularProgressButton
    private val btnDelete: CircularProgressButton
    private val btnCancel: Button
    private val topText: TextView

    private val name: TextInputLayout

    init {
        val view = LayoutInflater.from(context).inflate(LAYOUT, null)

        btnSave = view.findViewById(R.id.save_button)
        btnDelete = view.findViewById(R.id.delete_button)
        btnCancel = view.findViewById(R.id.cancel_button)
        topText = view.findViewById(R.id.text)
        name = view.findViewById(R.id.name)

        dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        if (record.keepForever) {
            btnDelete.visibility = View.VISIBLE
            topText.setText(R.string.delete_or_rename_record)
            btnSave.setText(R.string.rename)
            name.editText?.setText(record.name)
        } else {
            btnDelete.visibility = View.GONE
            topText.setText(R.string.save_record)
            btnSave.setText(R.string.save)
        }
    }

    @UiThread
    fun getName(): String? {
        val s = name.editText?.text.toString()
        return if (s.isEmpty()) null else s
    }

    fun setOnSaveClickListener(l: () -> Unit) {
        btnSave.setOnClickListener { l() }
    }

    fun setOnCancelClickListener(l: () -> Unit) {
        btnCancel.setOnClickListener { l() }
    }

    fun setOnClickDeleteListener(l: () -> Unit) {
        btnDelete.setOnClickListener { l() }
    }

    @UiThread
    fun setState(state: State) {
        when (state) {
            State.Edit -> {
                btnDelete.isEnabled = true
                btnDelete.revertAnimation()

                btnSave.isEnabled = true
                btnSave.revertAnimation()
            }
            State.Saving -> {
                btnDelete.isEnabled = false
                btnSave.startAnimation()
            }
            State.Deleting -> {
                btnSave.isEnabled = false
                btnDelete.startAnimation()
            }
        }
    }

    enum class State {
        Edit,
        Saving,
        Deleting,
    }
}