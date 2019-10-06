package ru.vladislavsumin.cams.camera.controller

import ru.vladislavsumin.cams.camera.connection.AdvancedCameraConnection
import ru.vladislavsumin.cams.camera.protocol.CommandCode
import ru.vladislavsumin.cams.camera.protocol.CommandRepository
import ru.vladislavsumin.cams.camera.protocol.Msg
import ru.vladislavsumin.cams.domain.RecordManager
import ru.vladislavsumin.cams.domain.VideoEncoderService
import ru.vladislavsumin.cams.entity.CameraEntity
import ru.vladislavsumin.cams.utils.logger
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.nio.file.Path

class CameraRecorder(
        private val camera: CameraEntity,
        private val videoEncoderService: VideoEncoderService,
        private val recordManager: RecordManager,
        private val tmpDirPath: Path
) {
    private val connection = AdvancedCameraConnection(camera)
    private var onFinishRecordListener: ((e: Throwable?) -> Unit)? = null

    private lateinit var record: Path
    private lateinit var recordStream: OutputStream
    private var timestamp: Long = 0L

    private var lastMotionEvent: Long = System.currentTimeMillis()

    fun start() {
        log.debug("[Cam: ${camera.name}] Start recording")
        connection.setOnDisconnectListener(this::onConnectionClose)
        connection.setOnMsgReceivedListener(this::onMsgReceived)
        connection.setOnConnectedListener(this::onConnected)
        connection.openAsync("CR: ${camera.name}")
    }

    private fun onConnected() {
        timestamp = System.currentTimeMillis()
        record = tmpDirPath.resolve("${camera.name}_$timestamp.h264")
        recordStream = FileOutputStream(record.toFile())

        connection.write(CommandRepository.monitorClaim(connection.sessionId))
        connection.write(CommandRepository.monitorStart(connection.sessionId))
    }

    private fun onMsgReceived(msg: Msg) {
        when (msg.messageId) {
            CommandCode.MONITOR_CLAIM_RSP -> log.trace("[Cam: ${camera.name}] monitor claim response received")
            CommandCode.MONITOR_DATA -> {
                recordStream.write(msg.data)
                if (lastMotionEvent + MOTION_TIMEOUT < System.currentTimeMillis()) {
                    connection.close()
                }
            }
            else -> throw UnexpectedResponseException(msg)
        }
    }

    private fun onConnectionClose(e: Throwable?) {
        if (e == null) log.debug("[Cam: ${camera.name}] Finish recording")
        else log.warn("[Cam: ${camera.name}] Finish recording, width exception", e)

        try {
            recordStream.close()
            val output = tmpDirPath.resolve("${camera.name}_$timestamp.mp4")
            videoEncoderService.encode(record, output)
            record.toFile().delete()
            recordManager.add(output, camera, timestamp)
        } catch (e: Exception) {
            log.error("Exception on encoding video", e)
        }

        onFinishRecordListener?.invoke(e)
    }

    fun onMotionEvent() {
        lastMotionEvent = System.currentTimeMillis()
    }


    fun setOnFinishRecordListener(listener: (e: Throwable?) -> Unit) {
        onFinishRecordListener = listener
    }

    companion object {
        private val log = logger<CameraRecorder>()
        private const val MOTION_TIMEOUT = 15_000
    }
}