package ru.vladislavsumin.cams.camera.protocol.motion

data class MotionEvent(
        val type: MotionEventType,
        val status: MotionEventStatus
)