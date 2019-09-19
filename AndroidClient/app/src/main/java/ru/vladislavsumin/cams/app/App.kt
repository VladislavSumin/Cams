package ru.vladislavsumin.cams.app

import android.app.Application
import android.util.Log

@Suppress("unused")
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initDagger()
    }

    private fun initDagger() {
        Injector = DaggerAppComponent.builder()
            .appModule(AppModule(applicationContext))
            .build()
        Log.i(TAG, "Dagger initialized")
    }

    companion object {
        private val TAG = App::class.java.simpleName
    }
}