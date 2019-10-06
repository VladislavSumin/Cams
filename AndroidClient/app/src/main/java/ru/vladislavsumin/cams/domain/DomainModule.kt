package ru.vladislavsumin.cams.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun provideVibrationManager(context: Context): VibrationManagerI {
        return VibrationManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkDiscoveryManager(): NetworkDiscoveryManagerI {
        return NetworkDiscoveryManager()
    }
}