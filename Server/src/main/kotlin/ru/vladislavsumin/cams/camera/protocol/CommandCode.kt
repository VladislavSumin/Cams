package ru.vladislavsumin.cams.camera.protocol

import java.lang.Exception

/**
 * Command code list
 */
enum class CommandCode(val code: Int) {
    NULL(0),

    LOGIN_REQ(1000),
    LOGIN_RSP(1001),

    KEEPALIVE_REQ(1006),
    KEEPALIVE_RSP(1007),

    SYSINFO_REQ(1020),
    SYSINFO_RSP(1021),

    MONITOR_REQ(1410),
    MONITOR_RSP(1411),
    MONITOR_DATA(1412),
    MONITOR_CLAIM(1413),
    MONITOR_CLAIM_RSP(1414),

    GUARD_REQ(1500),
    GUARD_RSP(1501),

    UNGUARD_REQ(1502),
    UNGUARD_RSP(1503),

    ALARM_REQ(1504),
    ALARM_RSP(1505),
    NET_ALARM_RSP(1506),
    NET_ALARM_RSP2(1507),
    ALARM_CENTER_MSG_REQ(1508);

    companion object {
        fun fromCode(code: Int): CommandCode {
            return CommandCode.values().find { it.code == code } ?: throw CommandCodeNotFoundException(code)
        }
    }

    class CommandCodeNotFoundException(code: Int) : Exception("Unknown command code $code")
}