package ru.vladislavsumin.cams.domain.impl

import android.net.ConnectivityManager
import android.net.Network
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.internal.matchers.Any

internal class NetworkManagerTest {

    lateinit var mConnectivityManager: ConnectivityManager
    var mCallback: ConnectivityManager.NetworkCallback? = null

    lateinit var mNetworkManager: NetworkManager

    @BeforeEach
    fun beforeEach() {
        mConnectivityManager = mock(ConnectivityManager::class.java)
        `when`(mConnectivityManager.registerDefaultNetworkCallback(any()))
                .then {
                    if (mCallback != null) fail<Unit>("try to add second listener")
                    mCallback = it.arguments[0] as ConnectivityManager.NetworkCallback
                    it
                }

        `when`(mConnectivityManager.unregisterNetworkCallback(any(ConnectivityManager.NetworkCallback::class.java)))
                .then {
                    if (mCallback == null) fail<Unit>("try to remove second listener")
                    assertSame(mCallback, it.arguments[0])
                    mCallback = null
                    it
                }

        mNetworkManager = NetworkManager(mConnectivityManager)
    }

    @Test
    fun `single connect`() {
        val testObserver = mNetworkManager.observeNetworkConnected()
                .test()

        assertNotNull(mCallback)
        mCallback!!.onAvailable(mock(Network::class.java))

        testObserver.assertValue(true)
        testObserver.dispose()

        assertNull(mCallback)
    }

    @Test
    fun `single connect listen multiple value`() {
        val testObserver = mNetworkManager.observeNetworkConnected()
                .test()

        mCallback!!.onUnavailable()
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onAvailable(mock(Network::class.java))
        mCallback!!.onLost(mock(Network::class.java))

        testObserver.assertValueSequence(listOf(false, true, false))
        testObserver.dispose()
    }

    @Test
    fun `multiple connect`() {
        val testObserver1 = mNetworkManager.observeNetworkConnected()
                .test()

        mCallback!!.onUnavailable()

        val testObserver2 = mNetworkManager.observeNetworkConnected()
                .test()

        testObserver1.assertValue(false)
        testObserver2.assertValue(false)

        testObserver1.dispose()
        testObserver2.dispose()

    }
}