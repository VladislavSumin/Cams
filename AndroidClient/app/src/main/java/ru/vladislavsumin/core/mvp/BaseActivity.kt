package ru.vladislavsumin.core.mvp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.UiThread
import com.arellomobile.mvp.MvpAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.storage.CredentialStorage
import ru.vladislavsumin.cams.ui.login.LoginActivity
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity(), BaseView {
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var credentialStorage: CredentialStorage

    @UiThread
    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    @UiThread
    protected fun Disposable.autoDispose() {
        disposables.add(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.inject(this)

        // check if not login, then open login activity
        if (!credentialStorage.hasServerAddress && this !is LoginActivity) {
            startActivity(LoginActivity.getLaunchIntent(this))
            finish()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setupUi()
        setupUx()
    }

    protected open fun setupUi() {}
    protected open fun setupUx() {}

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun showToast(text: String, duration: Int) {
        Toast.makeText(this, text, duration).show()
    }

    override fun startActivity(factory: (context: Context) -> Intent) {
        startActivity(factory(this))
    }

    protected fun setFullscreen(enabled: Boolean) {
        if (enabled) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }
}