package ru.vladislavsumin.cams.domain.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.cams.domain.interfaces.NetworkManagerI

class NetworkManager(mContext: Context) : NetworkManagerI {
    private val mConnectivityService =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val mNetworkConnected: Observable<Boolean> = Observable.create<Boolean> {
        val callback = NetworkCallback(it)
        mConnectivityService.registerDefaultNetworkCallback(callback)
        it.setCancellable { mConnectivityService.unregisterNetworkCallback(callback) }
    }
            .distinctUntilChanged()
            .doOnNext { Log.d("TAG", "network state: $it") }
            .share()

    override fun observeNetworkConnected(): Observable<Boolean> = mNetworkConnected

    private inner class NetworkCallback(private val emitter: ObservableEmitter<Boolean>) :
            ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            emitter.onNext(true)
        }

        override fun onUnavailable() {
            emitter.onNext(false)
        }

        override fun onLost(network: Network?) {
            emitter.onNext(false)
        }
    }
}