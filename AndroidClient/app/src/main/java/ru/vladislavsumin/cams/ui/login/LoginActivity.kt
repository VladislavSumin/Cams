package ru.vladislavsumin.cams.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_login.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.domain.interfaces.VibrationManagerI
import ru.vladislavsumin.cams.dto.ServerInfoDTO
import ru.vladislavsumin.core.mvp.BaseActivity
import ru.vladislavsumin.cams.ui.view.WaitDialog
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView {
    companion object {
        const val LAYOUT = R.layout.activity_login

        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val dialog: WaitDialog by lazy { WaitDialog(this) }

    @InjectPresenter
    lateinit var mPresenter: LoginPresenter

    @Inject
    lateinit var vibrationManager: VibrationManagerI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT)

        Injector.inject(this)
    }

    override fun setupUi() {
        super.setupUi()
        server_list.divider = null
    }

    override fun setupUx() {
        super.setupUx()
        login.setOnClickListener { mPresenter.login(address.text.toString()) }
        address.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                login.isEnabled = s.isNotEmpty()
            }
        })

        server_list.setOnItemClickListener { _, view, _, _ ->
            address.setText((view as TextView).text)
        }
    }

    override fun showCheckConnectionDialog() {
        dialog.show()
    }

    override fun dismissCheckConnectionDialog() {
        dialog.dismiss()
    }

    override fun setConnectionDialogStateToWait() {
        dialog.setState(WaitDialog.State.Wait, R.string.check_connection)
    }

    override fun setConnectionDialogStateToSuccess() {
        vibrationManager.vibrateShort()
        dialog.setState(WaitDialog.State.Success, R.string.check_connection_success)
    }

    override fun setConnectionDialogStateToError() {
        vibrationManager.vibrateShort()
        dialog.setState(WaitDialog.State.Error, R.string.check_connection_error)
    }

    override fun setServerList(list: List<ServerInfoDTO>) {
        newtwork_discovery_text.visibility = View.VISIBLE
        val array = list.map { it.address }.toTypedArray()
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array)
        server_list.adapter = adapter
    }
}