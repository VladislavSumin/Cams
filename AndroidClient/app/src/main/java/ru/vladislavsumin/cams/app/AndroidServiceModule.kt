package ru.vladislavsumin.cams.app

import android.content.Context
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidServiceModule {
    @Provides
    @Singleton
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}