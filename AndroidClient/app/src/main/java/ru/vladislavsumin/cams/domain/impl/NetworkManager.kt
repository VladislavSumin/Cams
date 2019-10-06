package ru.vladislavsumin.cams.domain.impl

import android.content.Context
import ru.vladislavsumin.cams.domain.interfaces.NetworkManagerI

class NetworkManager(private val mContext: Context) : NetworkManagerI {
//    private val mConnectivityService =
//            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//    private val mNetworkStateReceiver = NetworkStateReceiver()
//
//    private val networkState = Observable.create<NetworkState> {
//        mContext.registerReceiver(mNetworkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
//      TODO
//    }
//            .startWith { getCurrentNetworkState() }
//            .publish()
//
//    private fun getCurrentNetworkState(): NetworkState {
//
//
//        return TODO()
//    }
//
//    enum class NetworkState {
//
//    }
//
//    private inner class NetworkStateReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent?) {
//        }
//    }
}