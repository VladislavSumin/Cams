package ru.vladislavsumin.cams.domain.impl

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.cams.domain.interfaces.NetworkManagerI

class NetworkManager(private val mConnectivityService: ConnectivityManager) : NetworkManagerI {
    private val mNetworkConnected: Observable<Boolean> = Observable.create<Boolean> {
        val callback = NetworkCallback(it)
        mConnectivityService.registerDefaultNetworkCallback(callback)
        it.setCancellable { mConnectivityService.unregisterNetworkCallback(callback) }
    }
            .distinctUntilChanged()
            .doOnNext { Log.d("TAG", "network state: $it") }
            .replay(1)
            .refCount()

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