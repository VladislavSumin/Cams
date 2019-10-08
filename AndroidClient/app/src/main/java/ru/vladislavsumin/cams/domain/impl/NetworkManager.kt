package ru.vladislavsumin.cams.domain.impl

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.cams.domain.interfaces.NetworkManagerI
import ru.vladislavsumin.core.utils.tag

class NetworkManager(private val mConnectivityService: ConnectivityManager) : NetworkManagerI {
    companion object {
        private val TAG = tag<NetworkManager>()
    }

    private val mNetworkConnectedObservable: Observable<Boolean> = Observable.create<Boolean> {
        val callback = NetworkCallback(it)
        mConnectivityService.registerDefaultNetworkCallback(callback)
        it.setCancellable { mConnectivityService.unregisterNetworkCallback(callback) }
    }
            .distinctUntilChanged()
            .doOnSubscribe { Log.d(TAG, "start monitoring network") }
            .doOnNext { Log.d(TAG, "connection state: $it") }
            .doOnDispose { Log.d(TAG,"stop monitoring network") }
            .replay(1)
            .refCount()

    override fun observeNetworkConnected(): Observable<Boolean> = mNetworkConnectedObservable

    private inner class NetworkCallback(private val emitter: ObservableEmitter<Boolean>) :
            ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            emitter.onNext(true)
        }

        override fun onUnavailable() {
            emitter.onNext(false)
        }

        override fun onLost(network: Network) {
            emitter.onNext(false)
        }
    }
}