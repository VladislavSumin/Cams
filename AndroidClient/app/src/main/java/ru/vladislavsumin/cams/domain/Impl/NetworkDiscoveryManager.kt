package ru.vladislavsumin.cams.domain.Impl

import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.vladislavsumin.cams.domain.interfaces.NetworkDiscoveryManagerI
import ru.vladislavsumin.cams.dto.ServerInfoDTO
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

    private val scanner = Observable.create<List<ServerInfoDTO>> {
        Log.d(TAG, "Start scan network")
        val scanner = Scanner(it)
        scanner.run()
        Log.d(TAG, "Stop scan network")
    }
            .subscribeOnIo()
            .share()

    override fun scan(): Observable<List<ServerInfoDTO>> {
        return scanner
    }

    private class Scanner(private val emitter: ObservableEmitter<List<ServerInfoDTO>>) {
        private lateinit var socket: DatagramSocket
        private val datagramPacket = DatagramPacket(ByteArray(PACKET_SIZE), PACKET_SIZE)
        private val servers: MutableList<ServerInfoDTO> = mutableListOf()

        fun run() {
            socket = DatagramSocket()
            socket.use {
                socket.soTimeout = TIMEOUT
                while (!emitter.isDisposed) {
                    try {
                        sendRequest()
                        processResponse()
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }
        }

        private fun sendRequest() {
            Log.v(TAG, "send request packet")
            datagramPacket.address = InetAddress.getByName("255.255.255.255")
            datagramPacket.port = REMOTE_PORT
            socket.send(datagramPacket)
        }

        private fun processResponse() {
            while (true) {
                try {
                    socket.receive(datagramPacket)
                    val serverInfoDTO = ServerInfoDTO.fromByteArray(datagramPacket.data)
                    Log.v(TAG, "receive response from ${datagramPacket.address}, response=$serverInfoDTO")
                    if (!servers.contains(serverInfoDTO)) {
                        Log.d(TAG, "find new server: $serverInfoDTO")
                        servers += serverInfoDTO
                        emitter.onNext(servers)
                    }
                } catch (e: SocketTimeoutException) {
                    break
                }
            }
        }
    }
}