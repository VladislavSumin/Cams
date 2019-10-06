package ru.vladislavsumin.cams.camera.connection

import ru.vladislavsumin.cams.camera.protocol.CommandCode
import ru.vladislavsumin.cams.camera.protocol.CommandRepository
import ru.vladislavsumin.cams.camera.protocol.Msg
import ru.vladislavsumin.cams.entity.CameraEntity
import ru.vladislavsumin.cams.utils.CalledOnThread
import ru.vladislavsumin.cams.utils.ThreadSafe
import ru.vladislavsumin.cams.utils.logger
import java.util.*

/**
 * Connect, authenticate, receive msg and ping
 */
class AdvancedCameraConnection(private val camera: CameraEntity, timeout: Int = 10_000) : AutoCloseable {
    private var connection = CameraConnection(camera, timeout)

    private var timer: Timer? = null
    private var pingTask: PingTask? = null

    private var onDisconnectListener: ((e: Throwable?) -> Unit)? = null
    private var onConnectedListener: (() -> Unit)? = null
    private var onMsgReceivedListener: ((msg: Msg) -> Unit)? = null

    var sessionId: Int = 0
        private set

    fun openAsync(threadName: String? = null) {
        Thread({
            open()
        }, threadName ?: "CC: ${camera.name}").start()
    }

    /**
     * Open connection and lock this thread to listen socket
     */
    @CalledOnThread("Msg listener thread")
    fun open() {
        synchronized(this) {
            try {
                connection.setOnDisconnectListener(this::onDisconnect)
                connection.open()
                auth()
                setupPing()
                log.debug("[Cam: ${camera.name}] connected")
                onConnectedListener?.invoke()
            } catch (e: Exception) {
                connection.disconnect(e)
            }
        }
        startListen()
    }

    private fun startListen() {
        try {
            while (true) readMsg()
        } catch (e: Exception) {
            connection.disconnect(e)
        }
    }

    private fun readMsg() {
        val msg = connection.read()
        when (msg.messageId) {
            CommandCode.LOGIN_RSP -> log.trace("[Cam: ${camera.name}] authenticated")
            CommandCode.KEEPALIVE_RSP -> log.trace("[Cam: ${camera.name}] keepAlive response received")
            else -> onMsgReceivedListener?.invoke(msg)
        }
    }

    private fun setupPing() {
        timer = Timer()
        pingTask = PingTask()
        timer!!.schedule(pingTask, 2000, 10000)
    }

    private fun auth() {
        connection.write(CommandRepository.auth())
        val read = connection.read()
        sessionId = read.sessionId
    }

    @ThreadSafe
    fun write(msg: Msg) {
        connection.write(msg)
    }

    @ThreadSafe
    override fun close() {
        connection.close()
    }

    @Synchronized
    private fun onDisconnect(e: Throwable?) {
        pingTask?.cancel()
        timer?.cancel()
        onDisconnectListener?.invoke(e)
    }

    /**
     * Call single after connect and authenticate
     */
    @CalledOnThread("Msg listener thread")
    fun setOnConnectedListener(listener: () -> Unit) {
        onConnectedListener = listener
    }

    /**
     * Call single after disconnect
     */
    fun setOnDisconnectListener(listener: (e: Throwable?) -> Unit) {
        onDisconnectListener = listener
    }

    /**
     * Call on msg received
     */
    @CalledOnThread("Msg listener thread")
    fun setOnMsgReceivedListener(listener: (msg: Msg) -> Unit) {
        onMsgReceivedListener = listener
    }

    private inner class PingTask : TimerTask() {
        override fun run() {
            connection.write(CommandRepository.keepAlive(sessionId))
        }
    }

    companion object {
        private val log = logger<AdvancedCameraConnection>()
    }
}