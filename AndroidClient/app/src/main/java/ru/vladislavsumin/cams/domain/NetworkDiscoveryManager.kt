package ru.vladislavsumin.cams.domain

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import ru.vladislavsumin.core.utils.subscribeOnIo
import ru.vladislavsumin.core.utils.tag
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class NetworkDiscoveryManager : NetworkDiscoveryManagerI {
    companion object {
        private val TAG = tag<NetworkDiscoveryManager>()
        private const val PACKET_SIZE = 1024
        private const val REMOTE_PORT = 8923
        private const val TIMEOUT = 7000
    }

    private var socket: DatagramSocket? = null
    private var datagramPacket: DatagramPacket? = null

    private val scanner = Observable.create<Unit> {
        Log.d(TAG, "Start scan network")
        openSocket()
        while (!it.isDisposed) {
            try {
                sendRequest()
                processResponse()
            } catch (e: InterruptedException) {
                break
            }
        }
        closeSocket()
        Log.d(TAG, "Stop scan network")
    }
            .subscribeOnIo()
            .share()
            .ignoreElements()

    private fun sendRequest() {
        val socket1 = socket!!
        val datagramPacket1 = datagramPacket!!

        Log.v(TAG, "Send request packet")
        datagramPacket1.address = InetAddress.getByName("255.255.255.255")
        datagramPacket1.port = REMOTE_PORT
        socket1.send(datagramPacket1)
    }

    private fun processResponse() {
        val socket1 = socket!!
        val datagramPacket1 = datagramPacket!!
        socket1.soTimeout = TIMEOUT
        while (true) {
            try {
                socket1.receive(datagramPacket1)
                Log.d(TAG, "RECEIVED RESPONSE ${datagramPacket1.address}")//TODO
            } catch (e: SocketTimeoutException) {
                break
            }
        }
    }

    private fun openSocket() {
        socket = DatagramSocket()
        datagramPacket = DatagramPacket(ByteArray(PACKET_SIZE), PACKET_SIZE)
    }

    private fun closeSocket() {
        socket!!.close()
        socket = null
        datagramPacket = null
    }


    override fun scan(): Completable {
        return scanner
    }
}