package ru.vladislavsumin.cams.camera.protocol

import com.fasterxml.jackson.databind.ObjectMapper

object CommandRepository {
    private val objectMapper = ObjectMapper()

    fun auth() = compile(
            CommandCode.LOGIN_REQ, 0,
            "EncryptType" to "MD5",
            "LoginType" to "DVRIP-Web",
            "PassWord" to "tlJwpbo6",
            "UserName" to "admin"
    )

    fun keepAlive(sessionID: Int) = compile(
            CommandCode.KEEPALIVE_REQ, sessionID,
            "Name" to "KeepAlive",
            "SessionID" to "0x%X".format(sessionID)
    )

    fun monitorClaim(sessionID: Int) = compile(
            CommandCode.MONITOR_CLAIM, sessionID,
            "Name" to "OPMonitor",
            "OPMonitor" to mapOf(
                    "Action" to "Claim",
                    "Parameter" to mapOf(
                            "Channel" to 0,
                            "CombinMode" to "NONE",
                            "StreamType" to "Main",
                            "TransMode" to "TCP"
                    )
            ),
            "SessionID" to "0x%X".format(sessionID)
    )

    fun monitorStart(sessionID: Int) = compile(
            CommandCode.MONITOR_REQ, sessionID,
            "OPMonitor" to mapOf(
                    "Action" to "Start",
                    "Parameter" to mapOf(
                            "Channel" to 0,
                            "CombinMode" to "NONE",
                            "StreamType" to "Main",
                            "TransMode" to "TCP"
                    )
            ),
            "SessionID" to "0x%X".format(sessionID)
    )

    fun monitorStop(sessionID: Int) = compile(
            CommandCode.MONITOR_REQ, sessionID,
            "OPMonitor" to mapOf(
                    "Action" to "Stop",
                    "Parameter" to mapOf(
                            "Channel" to 0,
                            "CombinMode" to "NONE",
                            "StreamType" to "Main",
                            "TransMode" to "TCP"
                    )
            ),
            "SessionID" to "0x%X".format(sessionID)
    )

    fun alarmStart(sessionID: Int) = compile(
            CommandCode.GUARD_REQ, sessionID,
            "SessionID" to "0x%X".format(sessionID)
    )

    private fun compile(commandCode: CommandCode, sessionID: Int, vararg pairs: Pair<String, Any>) =
            compile(commandCode, sessionID, pairs.toMap())

    private fun compile(commandCode: CommandCode, sessionID: Int, map: Map<String, Any>) =
            newInstance(commandCode, map.toJsonString().toByteArray(), sessionID)

    private fun newInstance(messageId: CommandCode, data: ByteArray, sessionId: Int = 0) =
            Msg(
                    messageId = messageId,
                    dataLength = data.size,
                    data = data,
                    sessionId = sessionId
            )

    private fun Map<String, Any>.toJsonString() = objectMapper.writeValueAsString(this)
}