package ru.vladislavsumin.cams.domain.interfaces

interface VibrationManagerI {

    fun vibrateShort()
    fun vibrate(duration: Long)
}