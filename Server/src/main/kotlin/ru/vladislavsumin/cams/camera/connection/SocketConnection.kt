package ru.vladislavsumin.cams.camera.connection

import ru.vladislavsumin.cams.utils.NotThreadSafe
import ru.vladislavsumin.cams.utils.ThreadSafe
import java.net.ConnectException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeoutException

/**
 * Just socket connection with timeout
 */
open class SocketConnection(
        private val address: String,
        private val port: Int,
        private val timeout: Int = 10_000) : AutoCloseable, ByteChannel {

    private val socketChannel = SocketChannel.open()

    /**
     * Open connection
     */
    @Throws(TimeoutException::class, ConnectException::class)
    fun open() {
        socketChannel.configureBlocking(false)
        socketChannel.connect(InetSocketAddress(address, port))

        //wait timeout
        val startConnectionTime = System.currentTimeMillis()
        while (startConnectionTime + timeout > System.currentTimeMillis()) {
            if (socketChannel.finishConnect()) break
            Thread.sleep(THREAD_DELAY_TIME)
        }

        if (!socketChannel.isConnected) {
            socketChannel.close()
            throw TimeoutException()
        }

        socketChannel.configureBlocking(true)
    }

    override fun close() = socketChannel.close()

    override fun isOpen(): Boolean = socketChannel.isOpen


    @ThreadSafe
    @Synchronized
    override fun write(src: ByteBuffer): Int {
        val result = src.remaining()
        while (src.hasRemaining()) socketChannel.write(src)
        return result
    }

    @NotThreadSafe
    override fun read(dst: ByteBuffer): Int {
        val result = dst.remaining()
        while (dst.hasRemaining()) socketChannel.read(dst)
        return result
    }

    fun isConnected() = socketChannel.isConnected

    companion object {
        private const val THREAD_DELAY_TIME: Long = 10
    }
}