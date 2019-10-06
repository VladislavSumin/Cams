package ru.vladislavsumin.cams.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.vladislavsumin.cams.dto.ServerInfoDTO
import ru.vladislavsumin.cams.utils.logger
import java.lang.RuntimeException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import javax.annotation.PreDestroy
import kotlin.concurrent.thread

@Service
class NetworkDiscoveryManager @Autowired constructor(
        @Value("\${pEnableServerDiscovery}") private val discoveryEnabled: Boolean,
        @Value("\${pServerDiscoveryAddress}") private val serverAddress: String
) {
    companion object {
        private val log = logger<NetworkDiscoveryManager>()
        private const val PORT = 8923
        private const val BUFFER_SIZE = 1024
    }

    private lateinit var socket: DatagramSocket
    private lateinit var datagramPacket: DatagramPacket
    private lateinit var serverInfoDTO: ServerInfoDTO

    init {
        if (discoveryEnabled) {
            start()
        } else {
            log.info("Server discovery disabled")
        }
    }

    private fun start() {
        serverInfoDTO = ServerInfoDTO(serverAddress)
        val serverNameSize = serverInfoDTO.toByteArray().size
        if (serverNameSize > BUFFER_SIZE) {
            val errorMessage = "Server name size is $serverNameSize, max size is $BUFFER_SIZE"
            log.error(errorMessage)
            throw RuntimeException(errorMessage)
        }
        datagramPacket = DatagramPacket(ByteArray(BUFFER_SIZE), BUFFER_SIZE)
        socket = DatagramSocket(PORT)

        log.info("Open socket: port $PORT")
        thread {
            try {
                while (true) processRequest()
            } catch (e: SocketException) {
                log.info("Socket closed")
            }
        }
    }

    private fun processRequest() {
        socket.receive(datagramPacket)
        log.trace("Receive request from ${datagramPacket.address}")
        datagramPacket.data = serverInfoDTO.toByteArray()
        socket.send(datagramPacket)
    }

    @PreDestroy
    fun onDestroy() {
        socket.close()
    }
}