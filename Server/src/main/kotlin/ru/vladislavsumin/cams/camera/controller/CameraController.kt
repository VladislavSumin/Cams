package ru.vladislavsumin.cams.camera.controller

import ru.vladislavsumin.cams.camera.connection.AdvancedCameraConnection
import ru.vladislavsumin.cams.camera.protocol.CommandCode
import ru.vladislavsumin.cams.camera.protocol.CommandRepository
import ru.vladislavsumin.cams.camera.protocol.Msg
import ru.vladislavsumin.cams.camera.protocol.motion.MotionEvent
import ru.vladislavsumin.cams.camera.protocol.motion.MotionEventJsonUtils
import ru.vladislavsumin.cams.entity.CameraEntity
import ru.vladislavsumin.cams.utils.logger
import java.net.SocketException

class CameraController(private val camera: CameraEntity, private val cameraRecorderFactory: CameraRecorderFactory) {

    private var cameraRecorder: CameraRecorder? = null
    private lateinit var connection: AdvancedCameraConnection

    private var isEnabled = false

    private lateinit var loop: Thread

    fun start() {
        log.info("Start CC for ${camera.name}")
        isEnabled = true
        loop = Thread(this::loop, "CC: ${camera.name}")
        loop.start()
    }

    @Synchronized
    fun stop() {
        isEnabled = false
        connection.close()
        loop.interrupt()
        loop.join()
        log.info("Stop CC for ${camera.name}")
    }

    private fun loop() {
        while (isEnabled) {
            synchronized(this) {
                if (!isEnabled) return
                connection = AdvancedCameraConnection(camera)
                connection.setOnConnectedListener(this::onConnect)
                connection.setOnDisconnectListener(this::onDisconnect)
                connection.setOnMsgReceivedListener(this::onMsgReceived)
            }
            connection.open()
            if (isEnabled) try {
                Thread.sleep(RECONNECT_TIMEOUT)
            } catch (e: InterruptedException) {
                return
            }
        }
    }

    private fun onConnect() {
        enableMotionEvent()
    }

    private fun enableMotionEvent() {
        connection.write(CommandRepository.alarmStart(connection.sessionId))
    }

    private fun onDisconnect(e: Throwable?) {
        if (e == null) log.debug("[Cam: ${camera.name}] Disconnected")
        else when (e) {
            is SocketException -> log.warn("[Cam: ${camera.name}] Disconnected width exception: {}", e.toString())
            else -> log.warn("[Cam: ${camera.name}] Disconnected width exception", e)
        }
    }

    private fun onMsgReceived(msg: Msg) {
        when (msg.messageId) {
            CommandCode.GUARD_RSP -> log.trace("[Cam: ${camera.name}] Motion event enabled")
            CommandCode.ALARM_REQ -> onMotionEvent(MotionEventJsonUtils.fromJson(msg.getDataAsString()))
            else -> throw UnexpectedResponseException(msg)
        }
    }

    private fun onMotionEvent(event: MotionEvent) {
        log.trace("[Cam: ${camera.name}] $event received")

        val recorder = cameraRecorder
        if (recorder != null) recorder.onMotionEvent()
        else {
            cameraRecorder = cameraRecorderFactory.createInstance(camera)
            //TODO normally close recorder at server shutdown
            cameraRecorder!!.setOnFinishRecordListener { cameraRecorder = null }
            cameraRecorder!!.start()
        }
    }

    companion object {
        private val log = logger<CameraController>()

        private const val RECONNECT_TIMEOUT: Long = 20_000
    }
}