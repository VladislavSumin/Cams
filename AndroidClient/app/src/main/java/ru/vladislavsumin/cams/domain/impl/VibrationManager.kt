package ru.vladislavsumin.cams.domain.impl

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import ru.vladislavsumin.cams.domain.interfaces.VibrationManagerI

class VibrationManager(context: Context) : VibrationManagerI {
    companion object {
        private const val DURATION_SHORT = 50L
    }

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    override fun vibrateShort() = vibrate(DURATION_SHORT)

    override fun vibrate(duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

}