package ru.vladislavsumin.cams.camera.connection

import ru.vladislavsumin.cams.camera.protocol.ChannelMsgDecoder.decode
import ru.vladislavsumin.cams.camera.protocol.ChannelMsgDecoder.encode
import ru.vladislavsumin.cams.camera.protocol.Msg
import ru.vladislavsumin.cams.dao.CameraDAO
import ru.vladislavsumin.cams.utils.NotThreadSafe
import ru.vladislavsumin.cams.utils.ThreadSafe
import ru.vladislavsumin.cams.utils.logger
import java.util.concurrent.atomic.AtomicBoolean

class CameraConnection(private val camera: CameraDAO, timeout: Int = 10_000) : AutoCloseable {

    private val socketConnection = SocketConnection(camera.ip, camera.port, timeout)
    private val isConnected = AtomicBoolean(true)

    private var onDisconnectListener: ((e: Throwable?) -> Unit)? = null

    fun open() {
        socketConnection.open()
        log.trace("[Cam: ${camera.name}] Connection established")
    }

    @ThreadSafe
    override fun close() = disconnect()

    /**
     * try to read msg
     * throw exception && disconnect on error
     */
    @NotThreadSafe
    fun read(): Msg {
        try {
            val msg = decode(socketConnection)
            return msg
        } catch (e: Exception) {
            disconnect(e)
            throw e
        }
    }

    /**
     * try to write msg
     * disconnect on error
     */
    @ThreadSafe
    fun write(msg: Msg) {
        try {
            encode(socketConnection, msg)
        } catch (e: Exception) {
            disconnect(e)
        }
    }

    /**
     * Close connection && trigger onDisconnect()
     */
    @ThreadSafe
    fun disconnect(e: Throwable? = null) {
        if (!isConnected.getAndSet(false)) return

        if (e == null) log.trace("[Cam: ${camera.name}] Connection closed")
        else log.trace("[Cam: ${camera.name}] Connection closed width exception", e)

        socketConnection.close()
        onDisconnectListener?.invoke(e)
    }

    /**
     * Call single time on disconnect
     */
    fun setOnDisconnectListener(listener: (e: Throwable?) -> Unit) {
        onDisconnectListener = listener
    }

    companion object {
        private val log = logger<CameraConnection>()
    }
}
