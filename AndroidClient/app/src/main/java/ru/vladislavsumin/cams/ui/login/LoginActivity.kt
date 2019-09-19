package ru.vladislavsumin.cams.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_login.*
import ru.vladislavsumin.cams.R
import ru.vladislavsumin.core.mvp.BaseActivity
import ru.vladislavsumin.cams.ui.view.WaitDialog

class LoginActivity : BaseActivity(), LoginView {
    companion object {
        fun getLaunchIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val dialog: WaitDialog by lazy { WaitDialog(this) }

    @InjectPresenter
    lateinit var mPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //TODO fix
        login.isEnabled = false
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
        dialog.setState(WaitDialog.State.Success, R.string.check_connection_success)
    }

    override fun setConnectionDialogStateToError() {
        dialog.setState(WaitDialog.State.Error, R.string.check_connection_error)
    }
}