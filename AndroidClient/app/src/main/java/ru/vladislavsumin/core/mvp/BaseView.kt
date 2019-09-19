package ru.vladislavsumin.core.mvp

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.arellomobile.mvp.MvpView

interface BaseView : MvpView {
    fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT)
    fun startActivity(factory: (context: Context) -> Intent)

    fun finish()
}