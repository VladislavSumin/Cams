package ru.vladislavsumin.cams.domain

interface VibrationManagerI {

    fun vibrateShort()
    fun vibrate(duration: Long)
}