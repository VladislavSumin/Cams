package ru.vladislavsumin.cams.ui.login

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import io.reactivex.Completable
import io.reactivex.Single
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.domain.interfaces.ConnectionManagerI
import ru.vladislavsumin.cams.domain.interfaces.NetworkDiscoveryManagerI
import ru.vladislavsumin.cams.storage.CredentialStorage
import ru.vladislavsumin.core.mvp.BasePresenter
import ru.vladislavsumin.cams.ui.MainActivity
import ru.vladislavsumin.core.utils.observeOnMainThread
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class LoginPresenter : BasePresenter<LoginView>() {
    companion object {
        private val TAG = tag<LoginPresenter>()

        private const val UI_CONNECT_DELAY = 1000L
        private const val UI_SUCCESS_DELAY = 1000L
        private const val UI_ERROR_DELAY = 1500L
    }

    @Inject
    lateinit var connectionManager: ConnectionManagerI

    @Inject
    lateinit var credentialStorage: CredentialStorage

    @Inject
    lateinit var networkDiscoveryManager: NetworkDiscoveryManagerI

    init {
        Injector.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (credentialStorage.hasServerAddress) {
            viewState.startActivity { MainActivity.getLaunchIntent(it) }
            viewState.finish()
            return
        }

        networkDiscoveryManager.scan()
                .observeOnMainThread()
                .subscribe { viewState.setServerList(it) }
                .autoDispose()
    }

    fun login(serverAddress: String) {
        viewState.setConnectionDialogStateToWait()
        viewState.showCheckConnectionDialog()

        Completable.mergeArrayDelayError(
                connectionManager.checkConnection(serverAddress)
                        .subscribeOnIo(),
                Completable.timer(UI_CONNECT_DELAY, TimeUnit.MILLISECONDS)
        )
                .observeOnMainThread()
                .subscribe(
                        { onCheckConnectionSuccess(serverAddress) },
                        { onCheckConnectionError(it) }
                )
                .autoDispose()
    }

    private fun onCheckConnectionSuccess(serverAddress: String) {
        viewState.setConnectionDialogStateToSuccess()
        Single.timer(UI_SUCCESS_DELAY, TimeUnit.MILLISECONDS)
                .observeOnMainThread()
                .subscribe { _, _ ->
                    viewState.dismissCheckConnectionDialog()
                    credentialStorage.serverAddress = serverAddress
                    viewState.startActivity { MainActivity.getLaunchIntent(it) }
                    viewState.finish()
                }
                .autoDispose()
    }

    private fun onCheckConnectionError(e: Throwable) {
        Log.d(TAG, "Error on check server connection", e)
        viewState.setConnectionDialogStateToError()
        Single.timer(UI_ERROR_DELAY, TimeUnit.MILLISECONDS)
                .observeOnMainThread()
                .subscribe { _, _ ->
                    viewState.dismissCheckConnectionDialog()
                }
                .autoDispose()
    }
}